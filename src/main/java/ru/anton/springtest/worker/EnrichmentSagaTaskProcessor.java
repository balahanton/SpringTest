package ru.anton.springtest.worker;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.client.HttpClientErrorException;
import ru.anton.springtest.dto.UserEnrichmentClientRequestDto;
import ru.anton.springtest.exception.EnrichmentServiceClientException;
import ru.anton.springtest.exception.EnrichmentServiceUnavailableException;
import ru.anton.springtest.exception.EntityNotFoundException;
import ru.anton.springtest.mapper.UserMapper;
import ru.anton.springtest.model.SagaTask;
import ru.anton.springtest.model.SagaTaskStatus;
import ru.anton.springtest.model.User;
import ru.anton.springtest.repository.SagaTaskRepository;
import ru.anton.springtest.repository.UserRepository;
import ru.anton.springtest.service.EnrichmentServiceAdapter;
import ru.anton.springtest.service.UserService;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

@Slf4j
@Component
@RequiredArgsConstructor
public class EnrichmentSagaTaskProcessor {

    private final SagaTaskRepository sagaTaskRepository;
    private final UserRepository userRepository;
    private final EnrichmentServiceAdapter enrichmentServiceAdapter;
    private final UserMapper userMapper;
    private final TransactionTemplate transactionTemplate;
    private final UserService userService;

    @Value("${saga.enrichment-worker.batch-size:50}")
    private int batchSize;

    @Value("${saga.enrichment-worker.max-attempts:5}")
    private int maxAttempts;

    @Scheduled(fixedDelayString = "${saga.enrichment-worker.delay-ms:60000}")
    public void processPendingTasks() {
        processBatch(sagaTaskRepository.claimPendingTasks(batchSize), this::processTask, "Обработка задачи саги");
    }

    @Scheduled(fixedDelayString = "${saga.compensation-worker.delay-ms:60000}")
    public void retryFailedCompensations() {
        processBatch(sagaTaskRepository.claimCompensationFailedTasks(batchSize), this::retryCompensation, "Повторная компенсация");
    }

    private void processBatch(List<SagaTask> tasks, Consumer<SagaTask> action, String actionDescription) {
        for (SagaTask task : tasks) {
            try {
                action.accept(task);
            } catch (RuntimeException ex) {
                log.error("{} для userId {} прервана непредвиденной ошибкой, задача останется в статусе IN_PROGRESS " +
                                "и требует ручного вмешательства или отдельного механизма повторного захвата: {}",
                        actionDescription, task.getUserId(), ex.getMessage(), ex);
            }
        }
    }

    public void processTask(SagaTask task) {
        UserEnrichmentClientRequestDto request = userMapper.toEnrichmentCreateDto(task);

        try {
            enrichmentServiceAdapter.createEnrichment(request);
            handleSuccess(task);
        } catch (EnrichmentServiceUnavailableException ex) {
            log.warn("Вызов enrichment-сервиса не удался для userId {}: {}",
                    task.getUserId(), ex.getMessage());
            handleFailure(task);
        } catch (EnrichmentServiceClientException ex) {
            log.error("Enrichment-сервис отклонил запрос для userId {}, повтор не имеет смысла: {}",
                    task.getUserId(), ex.getMessage());
            executeCompensation(task);
        }
    }

    private void handleSuccess(SagaTask task) {
        try {
            transactionTemplate.executeWithoutResult(status -> applyEnrichmentDone(task));
        } catch (EntityNotFoundException ex) {
            log.error("Задача саги ссылается на несуществующего userId {}: {}. Данные рассинхронизированы, " +
                            "обогащение будет компенсировано без ретраев",
                    task.getUserId(), ex.getMessage());
            handleOrphanedTask(task);
            return;
        } catch (DataAccessException | TransactionException ex) {
            log.error("Обогащение выполнено, но не удалось сохранить результат для userId {}: {}",
                    task.getUserId(), ex.getMessage());
            return;
        }

        log.info("Обогащение успешно завершено для userId: {}", task.getUserId());
        evictCacheSafely(task.getUserId());
    }

    private void applyEnrichmentDone(SagaTask task) {
        task.setStatus(SagaTaskStatus.DONE);
        sagaTaskRepository.save(task);

        User user = userRepository.findById(task.getUserId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Пользователь не найден для saga-задачи обогащения, userId: " + task.getUserId()));
        user.setEnrichmentStatus(SagaTaskStatus.DONE);
        userRepository.save(user);
    }

    private void handleOrphanedTask(SagaTask task) {
        boolean cleanupSucceeded = tryDeleteEnrichment(task);

        if (!cleanupSucceeded) {
            markCompensationFailed(task);
            log.error("Не удалось зачистить enrichment-сервис для orphaned userId {}. Компенсация будет повторяться",
                    task.getUserId());
            return;
        }

        boolean saved = executeTransactionally(task.getUserId(), "Не удалось сохранить статус FAILED для orphaned userId", () -> {
            task.setStatus(SagaTaskStatus.FAILED);
            sagaTaskRepository.save(task);
        });
        if (!saved) {
            return;
        }

        log.error("Saga-задача для userId {} завершена как FAILED: пользователь не найден в БД", task.getUserId());
        evictCacheSafely(task.getUserId());
    }

    private void handleFailure(SagaTask task) {
        int attempts = task.getAttempts() + 1;
        task.setAttempts(attempts);
        log.warn("Попытка обогащения #{} для userId {} не удалась", attempts, task.getUserId());

        if (attempts >= maxAttempts) {
            executeCompensation(task);
        } else {
            saveWithStatus(task, SagaTaskStatus.PENDING);
        }
    }

    private void executeCompensation(SagaTask task) {
        boolean cleanupSucceeded = tryDeleteEnrichment(task);

        if (!cleanupSucceeded) {
            markCompensationFailed(task);
            log.error("Не удалось зачистить enrichment-сервис для userId {}, компенсация будет повторена", task.getUserId());
            return;
        }

        boolean saved = executeTransactionally(task.getUserId(), "Не удалось завершить компенсацию в БД для userId", () -> {
            userRepository.deleteById(task.getUserId());
            task.setStatus(SagaTaskStatus.FAILED);
            sagaTaskRepository.save(task);
        });
        if (!saved) {
            return;
        }

        log.error("Регистрация userId {} компенсирована (пользователь удалён) после {} неудачных попыток обогащения",
                task.getUserId(), task.getAttempts());
        evictCacheSafely(task.getUserId());
    }

    public void retryCompensation(SagaTask task) {
        executeCompensation(task);
    }

    private boolean tryDeleteEnrichment(SagaTask task) {
        try {
            enrichmentServiceAdapter.deleteEnrichment(task.getUserId());
            return true;
        } catch (HttpClientErrorException.NotFound ex) {
            log.info("Enrichment-запись для userId {} уже отсутствует во внешнем сервисе, считаем компенсацию выполненной",
                    task.getUserId());
            return true;
        } catch (HttpClientErrorException ex) {
            log.warn("Enrichment-сервис отклонил запрос на удаление для userId {}: {}",
                    task.getUserId(), ex.getMessage());
            return false;
        }
    }

    private void saveWithStatus(SagaTask task, SagaTaskStatus status) {
        transactionTemplate.executeWithoutResult(txStatus -> {
            task.setStatus(status);
            sagaTaskRepository.save(task);
        });
    }

    private boolean executeTransactionally(UUID userId, String errorMessagePrefix, Runnable action) {
        try {
            transactionTemplate.executeWithoutResult(status -> action.run());
            return true;
        } catch (DataAccessException | TransactionException ex) {
            log.error("{} {}: {}", errorMessagePrefix, userId, ex.getMessage());
            return false;
        }
    }

    private void markCompensationFailed(SagaTask task) {
        saveWithStatus(task, SagaTaskStatus.COMPENSATION_FAILED);
    }

    private void evictCacheSafely(UUID userId) {
        try {
            userService.evictUserCache(userId);
        } catch (Exception ex) {
            log.error("Результат саги сохранён в БД, но не удалось очистить кеш для userId {}: {}",
                    userId, ex.getMessage());
        }
    }
}
