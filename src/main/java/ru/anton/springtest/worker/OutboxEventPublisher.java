package ru.anton.springtest.worker;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import ru.anton.springtest.config.OutboxPublisherProperties;
import ru.anton.springtest.model.OutboxEvent;
import ru.anton.springtest.model.OutboxEventStatus;
import ru.anton.springtest.repository.OutboxEventRepository;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxEventPublisher {

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final TransactionTemplate transactionTemplate;
    private final OutboxPublisherProperties properties;

    @Scheduled(fixedDelayString = "${outbox.publisher.delay-ms:5000}")
    public void publishPendingEvents() {
        List<OutboxEvent> events = transactionTemplate.execute(status -> claimAndMarkProcessing());

        CompletableFuture<?>[] futures = events.stream()
                .map(this::publishEvent)
                .toArray(CompletableFuture[]::new);
        CompletableFuture.allOf(futures).join();
    }

    private List<OutboxEvent> claimAndMarkProcessing() {
        OffsetDateTime staleBefore = OffsetDateTime.now().minus(Duration.ofMillis(properties.getStaleTimeoutMs()));
        List<OutboxEvent> claimed = outboxEventRepository.claimPendingEvents(properties.getBatchSize(), staleBefore);

        OffsetDateTime now = OffsetDateTime.now();
        claimed.forEach(event -> {
            event.setStatus(OutboxEventStatus.PROCESSING);
            event.setClaimedAt(now);
            event.setClaimToken(UUID.randomUUID());
        });
        outboxEventRepository.saveAll(claimed);
        return claimed;
    }

    private CompletableFuture<Void> publishEvent(OutboxEvent event) {
        try {
            String targetTopic = resolveTopic(event.getEventType());
            ProducerRecord<String, String> record = buildRecord(event, targetTopic);

            return kafkaTemplate.send(record)
                    .thenAccept(result -> onSendSuccess(event, targetTopic))
                    .exceptionally(ex -> {
                        handleFailure(event, ex);
                        return null;
                    });
        } catch (Exception ex) {
            handleFailure(event, ex);
            return CompletableFuture.completedFuture(null);
        }
    }

    private ProducerRecord<String, String> buildRecord(OutboxEvent event, String targetTopic) {
        ProducerRecord<String, String> record = new ProducerRecord<>(
                targetTopic, null, event.getAggregateId().toString(), event.getPayload());
        record.headers().add(new RecordHeader("eventId", event.getId().toString().getBytes(StandardCharsets.UTF_8)));
        return record;
    }

    private void onSendSuccess(OutboxEvent event, String targetTopic) {
        transactionTemplate.executeWithoutResult(status -> {
            int updated = outboxEventRepository.markSent(event.getId(), event.getClaimToken(), OffsetDateTime.now());
            if (updated == 0) {
                log.warn("Событие {} уже было перехвачено другим воркером, статус SENT не применён", event.getId());
            }
        });
        log.info("Событие {} ({}) отправлено в топик {} для aggregateId {}",
                event.getId(), event.getEventType(), targetTopic, event.getAggregateId());
    }

    private String resolveTopic(String eventType) {
        String mapped = properties.getTopicMapping().get(eventType);
        if (mapped == null) {
            log.warn("Для eventType '{}' не найден явный маппинг топика, используется default-topic '{}'",
                    eventType, properties.getDefaultTopic());
            return properties.getDefaultTopic();
        }
        return mapped;
    }

    private void handleFailure(OutboxEvent event, Throwable ex) {
        int attempts = event.getAttempts() + 1;

        if (attempts >= properties.getMaxAttempts()) {
            log.error("Событие {} не удалось отправить после {} попыток, помечаем FAILED: {}",
                    event.getId(), attempts, ex.getMessage(), ex);
            transactionTemplate.executeWithoutResult(status -> {
                int updated = outboxEventRepository.markFailed(event.getId(), event.getClaimToken(), attempts);
                if (updated == 0) {
                    log.warn("Событие {} уже было перехвачено другим воркером, статус FAILED не применён", event.getId());
                }
            });
            return;
        }

        long delayMs = properties.getRetryBaseDelayMs() * (1L << (attempts - 1));
        OffsetDateTime nextAttemptAt = OffsetDateTime.now().plus(Duration.ofMillis(delayMs));

        log.warn("Попытка {} отправки события {} не удалась, следующая попытка в {}: {}",
                attempts, event.getId(), nextAttemptAt, ex.getMessage());

        transactionTemplate.executeWithoutResult(status -> {
            int updated = outboxEventRepository.markForRetry(event.getId(), event.getClaimToken(), attempts, nextAttemptAt);
            if (updated == 0) {
                log.warn("Событие {} уже было перехвачено другим воркером, повторная попытка не запланирована", event.getId());
            }
        });
    }

}
