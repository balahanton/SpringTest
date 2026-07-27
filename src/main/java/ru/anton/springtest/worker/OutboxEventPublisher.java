package ru.anton.springtest.worker;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import ru.anton.springtest.model.OutboxEvent;
import ru.anton.springtest.model.OutboxEventStatus;
import ru.anton.springtest.repository.OutboxEventRepository;

import java.time.OffsetDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxEventPublisher {

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final TransactionTemplate transactionTemplate;

    @Value("${outbox.publisher.batch-size:50}")
    private int batchSize;

    @Value("${outbox.publisher.topic:user.created}")
    private String topic;

    @Scheduled(fixedDelayString = "${outbox.publisher.delay-ms:5000}")
    public void publishPendingEvents() {
        List<OutboxEvent> events = transactionTemplate.execute(status ->
                outboxEventRepository.claimPendingEvents(batchSize));

        if (events == null) {
            return;
        }
        events.forEach(this::publishEvent);
    }

    private void publishEvent(OutboxEvent event) {
        try {
            kafkaTemplate.send(topic, event.getAggregateId().toString(), event.getPayload()).get();
            transactionTemplate.executeWithoutResult(status -> {
                event.setStatus(OutboxEventStatus.SENT);
                event.setSentAt(OffsetDateTime.now());
                outboxEventRepository.save(event);
            });
            log.info("Событие {} отправлено в Kafka для aggregateId {}", event.getId(), event.getAggregateId());
        } catch (Exception ex) {
            log.error("Не удалось отправить событие {} в Kafka: {}", event.getId(), ex.getMessage(), ex);
            transactionTemplate.executeWithoutResult(status -> {
                event.setAttempts(event.getAttempts() + 1);
                outboxEventRepository.save(event);
            });
        }
    }

}
