package ru.anton.springtest.worker;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.TransactionTemplate;
import ru.anton.springtest.dto.UserEnrichmentClientRequestDto;
import ru.anton.springtest.exception.EnrichmentServiceUnavailableException;
import ru.anton.springtest.mapper.UserMapper;
import ru.anton.springtest.model.SagaTask;
import ru.anton.springtest.model.SagaTaskStatus;
import ru.anton.springtest.repository.SagaTaskRepository;
import ru.anton.springtest.repository.UserRepository;
import ru.anton.springtest.service.EnrichmentServiceAdapter;
import ru.anton.springtest.service.UserService;

@Slf4j
@Component
@RequiredArgsConstructor
public class EnrichmentSagaTaskProcessor {

    private static final int MAX_ATTEMPTS = 5;

    private final SagaTaskRepository sagaTaskRepository;
    private final UserRepository userRepository;
    private final EnrichmentServiceAdapter enrichmentServiceAdapter;
    private final UserMapper userMapper;
    private final TransactionTemplate transactionTemplate;
    private final UserService userService;

    public void processTask(SagaTask task) {
        UserEnrichmentClientRequestDto request = userMapper.toEnrichmentCreateDto(task);

        boolean enrichmentSucceeded;
        try {
            enrichmentServiceAdapter.createEnrichment(request);
            enrichmentSucceeded = true;
        } catch (EnrichmentServiceUnavailableException ex) {
            enrichmentSucceeded = false;
            log.warn("Вызов enrichment-сервиса не удался для userId {}: {}",
                    task.getUserId(), ex.getMessage());
        }

        if (enrichmentSucceeded) {
            handleSuccess(task);
        } else {
            handleFailure(task);
        }
    }

    private void handleSuccess(SagaTask task) {
        try {
            transactionTemplate.executeWithoutResult(status -> {
                task.setStatus(SagaTaskStatus.DONE);
                sagaTaskRepository.save(task);

                userRepository.findById(task.getUserId()).ifPresent(user -> {
                    user.setEnrichmentStatus(SagaTaskStatus.DONE);
                    userRepository.save(user);
                });
            });

            userService.evictUserCache(task.getUserId());
            log.info("Обогащение успешно завершено для userId: {}", task.getUserId());

        } catch (DataAccessException | TransactionException ex) {
            log.error("Обогащение выполнено, но не удалось сохранить результат для userId {}: {}",
                    task.getUserId(), ex.getMessage());
        }
    }

    private void handleFailure(SagaTask task) {
        int attempts = task.getAttempts() + 1;
        task.setAttempts(attempts);
        log.warn("Попытка обогащения #{} для userId {} не удалась", attempts, task.getUserId());

        if (attempts >= MAX_ATTEMPTS) {
            rollback(task);
        } else {
            try {
                transactionTemplate.executeWithoutResult(status -> sagaTaskRepository.save(task));
            } catch (DataAccessException | TransactionException ex) {
                log.error("Не удалось сохранить количество попыток для userId {}: {}",
                        task.getUserId(), ex.getMessage());
            }
        }
    }

    private void rollback(SagaTask task) {
        boolean cleanupSucceeded = tryDeleteEnrichment(task);

        if (!cleanupSucceeded) {
            markCompensationFailed(task);
            log.error("Регистрация userId {} требует компенсации: не удалось зачистить enrichment-сервис после {} попыток обогащения. Компенсация будет повторяться",
                    task.getUserId(), task.getAttempts());
            return;
        }

        try {
            transactionTemplate.executeWithoutResult(status -> {
                userRepository.deleteById(task.getUserId());
                task.setStatus(SagaTaskStatus.FAILED);
                sagaTaskRepository.save(task);
            });

            userService.evictUserCache(task.getUserId());
            log.error("Регистрация userId {} отменена после {} неудачных попыток обогащения",
                    task.getUserId(), task.getAttempts());

        } catch (DataAccessException | TransactionException ex) {
            log.error("Не удалось выполнить откат регистрации для userId {}: {}",
                    task.getUserId(), ex.getMessage());
        }
    }

    public void retryCompensation(SagaTask task) {
        boolean cleanupSucceeded = tryDeleteEnrichment(task);

        if (!cleanupSucceeded) {
            markCompensationFailed(task);
            log.warn("Повторная попытка компенсации не удалась для userId {}, будет повторена позже", task.getUserId());
            return;
        }

        try {
            transactionTemplate.executeWithoutResult(status -> {
                userRepository.deleteById(task.getUserId());
                task.setStatus(SagaTaskStatus.FAILED);
                sagaTaskRepository.save(task);
            });

            userService.evictUserCache(task.getUserId());
            log.info("Компенсация успешно завершена для userId {}", task.getUserId());

        } catch (DataAccessException | TransactionException ex) {
            log.error("Компенсация во внешнем сервисе прошла, но не удалось завершить откат в БД для userId {}: {}",
                    task.getUserId(), ex.getMessage());
        }
    }

    private boolean tryDeleteEnrichment(SagaTask task) {
        try {
            enrichmentServiceAdapter.deleteEnrichment(task.getUserId());
            return true;
        } catch (Exception externalEx) {
            log.warn("Не удалось зачистить enrichment-сервис для userId {}: {}",
                    task.getUserId(), externalEx.getMessage());
            return false;
        }
    }

    private void markCompensationFailed(SagaTask task) {
        try {
            transactionTemplate.executeWithoutResult(status -> {
                task.setStatus(SagaTaskStatus.COMPENSATION_FAILED);
                sagaTaskRepository.save(task);
            });
        } catch (DataAccessException | TransactionException ex) {
            log.error("Не удалось сохранить статус COMPENSATION_FAILED для userId {}: {}",
                    task.getUserId(), ex.getMessage());
        }
    }
}
