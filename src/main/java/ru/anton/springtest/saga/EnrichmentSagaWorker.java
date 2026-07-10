package ru.anton.springtest.saga;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.anton.springtest.model.SagaTask;
import ru.anton.springtest.model.SagaTaskStatus;
import ru.anton.springtest.repository.SagaTaskRepository;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class EnrichmentSagaWorker {

    private final SagaTaskRepository sagaTaskRepository;
    private final EnrichmentSagaTaskProcessor taskProcessor;

    @Scheduled(fixedDelayString = "${saga.enrichment-worker.delay-ms:60000}")
    public void processPendingTasks() {
        List<SagaTask> tasks = sagaTaskRepository.findByStatus(SagaTaskStatus.PENDING);
        for (SagaTask task : tasks) {
            taskProcessor.processTask(task);
        }
    }
}
