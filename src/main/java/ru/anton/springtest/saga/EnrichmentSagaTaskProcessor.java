package ru.anton.springtest.saga;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.anton.springtest.config.RedisCacheConfig;
import ru.anton.springtest.dto.UserEnrichmentCreateClientDto;
import ru.anton.springtest.mapper.UserMapper;
import ru.anton.springtest.model.SagaTask;
import ru.anton.springtest.model.SagaTaskStatus;
import ru.anton.springtest.repository.SagaTaskRepository;
import ru.anton.springtest.repository.UserRepository;
import ru.anton.springtest.service.EnrichmentServiceAdapter;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class EnrichmentSagaTaskProcessor {

    private static final int MAX_ATTEMPTS = 5;

    private final SagaTaskRepository sagaTaskRepository;
    private final UserRepository userRepository;
    private final EnrichmentServiceAdapter enrichmentServiceAdapter;
    private final CacheManager cacheManager;
    private final UserMapper userMapper;

    @Transactional
    public void processTask(SagaTask task) {
        try {
            UserEnrichmentCreateClientDto request = userMapper.toEnrichmentCreateDto(task);

            enrichmentServiceAdapter.createEnrichment(request);

            task.setStatus(SagaTaskStatus.DONE);
            sagaTaskRepository.save(task);

            userRepository.findById(task.getUserId()).ifPresent(user -> {
                user.setEnrichmentStatus(SagaTaskStatus.DONE);
                userRepository.save(user);
            });

            evictUserCache(task.getUserId());
            log.info("Обогащение успешно завершено для userId: {}", task.getUserId());

        } catch (Exception ex) {
            int attempts = task.getAttempts() + 1;
            task.setAttempts(attempts);
            log.warn("Попытка обогащения #{} для userId {} не удалась: {}",
                    attempts, task.getUserId(), ex.getMessage());

            if (attempts >= MAX_ATTEMPTS) {
                rollback(task);
            } else {
                sagaTaskRepository.save(task);
            }
        }
    }

    private void rollback(SagaTask task) {
        try {
            enrichmentServiceAdapter.deleteEnrichment(task.getUserId());
        } catch (Exception externalEx) {
            log.warn("Не удалось зачистить enrichment-сервис для userId {}: {}",
                    task.getUserId(), externalEx.getMessage());
        }

        userRepository.deleteById(task.getUserId());

        task.setStatus(SagaTaskStatus.FAILED);
        sagaTaskRepository.save(task);

        evictUserCache(task.getUserId());
        log.error("Регистрация userId {} отменена после {} неудачных попыток обогащения",
                task.getUserId(), task.getAttempts());
    }

    private void evictUserCache(UUID userId) {
        Cache cache = cacheManager.getCache(RedisCacheConfig.USERS_CACHE);
        if (cache != null) {
            cache.evict(userId);
        }
    }
}
