package ru.anton.springtest.worker;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import ru.anton.springtest.model.OutboxEvent;
import ru.anton.springtest.model.OutboxEventStatus;
import ru.anton.springtest.repository.OutboxEventRepository;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
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

    @Value("${outbox.publisher.stale-timeout-ms:120000}")
    private long staleTimeoutMs;

    @Value("${outbox.publisher.max-attempts:5}")
    private int maxAttempts;

    @Value("${outbox.publisher.retry-base-delay-ms:2000}")
    private long retryBaseDelayMs;

    @Scheduled(fixedDelayString = "${outbox.publisher.delay-ms:5000}")
    public void publishPendingEvents() {
        List<OutboxEvent> events = transactionTemplate.execute(status -> claimAndMarkProcessing());
        events.forEach(this::publishEvent);
    }

    private List<OutboxEvent> claimAndMarkProcessing() {
        OffsetDateTime staleBefore = OffsetDateTime.now().minus(Duration.ofMillis(staleTimeoutMs));
        List<OutboxEvent> claimed = outboxEventRepository.claimPendingEvents(batchSize, staleBefore);

        OffsetDateTime now = OffsetDateTime.now();
        claimed.forEach(event -> {
            event.setStatus(OutboxEventStatus.PROCESSING);
            event.setClaimedAt(now);
        });
        outboxEventRepository.saveAll(claimed);
        return claimed;
    }

    private void publishEvent(OutboxEvent event) {
        try {
            ProducerRecord<String, String> record = new ProducerRecord<>(
                    topic, null, event.getAggregateId().toString(), event.getPayload());
            record.headers().add(new RecordHeader("eventId", event.getId().toString().getBytes(StandardCharsets.UTF_8)));
            kafkaTemplate.send(record).get();

            transactionTemplate.executeWithoutResult(status -> {
                event.setStatus(OutboxEventStatus.SENT);
                event.setSentAt(OffsetDateTime.now());
                outboxEventRepository.save(event);
            });
            log.info("Событие {} отправлено в Kafka для aggregateId {}", event.getId(), event.getAggregateId());
        } catch (Exception ex) {
            handleFailure(event, ex);
        }
    }

    private void handleFailure(OutboxEvent event, Exception ex) {
        int attempts = event.getAttempts() + 1;

        if (attempts >= maxAttempts) {
            log.error("Событие {} не удалось отправить после {} попыток, помечаем FAILED: {}",
                    event.getId(), attempts, ex.getMessage(), ex);
            transactionTemplate.executeWithoutResult(status -> {
                event.setStatus(OutboxEventStatus.FAILED);
                event.setAttempts(attempts);
                outboxEventRepository.save(event);
            });
            return;
        }

        long delayMs = retryBaseDelayMs * (1L << (attempts - 1));
        OffsetDateTime nextAttemptAt = OffsetDateTime.now().plus(Duration.ofMillis(delayMs));

        log.warn("Попытка {} отправки события {} не удалась, следующая попытка в {}: {}",
                attempts, event.getId(), nextAttemptAt, ex.getMessage());

        transactionTemplate.executeWithoutResult(status -> {
            event.setStatus(OutboxEventStatus.NEW);
            event.setAttempts(attempts);
            event.setNextAttemptAt(nextAttemptAt);
            outboxEventRepository.save(event);
        });
    }

}
