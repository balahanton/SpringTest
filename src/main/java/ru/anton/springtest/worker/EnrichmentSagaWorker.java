package ru.anton.springtest.worker;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.anton.springtest.model.SagaTask;
import ru.anton.springtest.repository.SagaTaskRepository;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class EnrichmentSagaWorker {

    private final SagaTaskRepository sagaTaskRepository;
    private final EnrichmentSagaTaskProcessor taskProcessor;

    @Value("${saga.enrichment-worker.batch-size:50}")
    private int batchSize;

    @Scheduled(fixedDelayString = "${saga.enrichment-worker.delay-ms:60000}")
    public void processPendingTasks() {
        List<SagaTask> tasks = sagaTaskRepository.claimPendingTasks(batchSize);
        for (SagaTask task : tasks) {
            taskProcessor.processTask(task);
        }
    }

    @Scheduled(fixedDelayString = "${saga.compensation-worker.delay-ms:60000}")
    public void retryFailedCompensations() {
        List<SagaTask> tasks = sagaTaskRepository.claimCompensationFailedTasks(batchSize);
        for (SagaTask task : tasks) {
            taskProcessor.retryCompensation(task);
        }
    }

}
