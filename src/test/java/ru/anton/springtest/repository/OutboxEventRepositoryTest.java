package ru.anton.springtest.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.anton.springtest.AbstractIntegrationTest;
import ru.anton.springtest.model.OutboxEvent;
import ru.anton.springtest.model.OutboxEventStatus;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("OutboxEventRepository — интеграционные тесты")
class OutboxEventRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    OutboxEventRepository outboxEventRepository;

    private OutboxEvent persistEvent(OutboxEventStatus status, OffsetDateTime claimedAt) {
        OutboxEvent event = new OutboxEvent();
        event.setEventType("TestEvent");
        event.setAggregateId(UUID.randomUUID());
        event.setPayload("{}");
        event.setStatus(status);
        event.setClaimedAt(claimedAt);
        return outboxEventRepository.save(event);
    }

    @Test
    @DisplayName("claimPendingEvents забирает NEW и просроченные PROCESSING, но не свежие PROCESSING")
    void claimPendingEvents_returnsNewAndStaleProcessing() {
        OutboxEvent newEvent = persistEvent(OutboxEventStatus.NEW, null);
        OutboxEvent staleProcessing = persistEvent(OutboxEventStatus.PROCESSING, OffsetDateTime.now().minusMinutes(10));
        persistEvent(OutboxEventStatus.PROCESSING, OffsetDateTime.now());

        List<OutboxEvent> claimed = outboxEventRepository.claimPendingEvents(
                10, OffsetDateTime.now().minusMinutes(2));

        assertThat(claimed)
                .extracting(OutboxEvent::getId)
                .containsExactlyInAnyOrder(newEvent.getId(), staleProcessing.getId());
    }

    @Test
    @DisplayName("markSent обновляет запись только при совпадении claimToken")
    void markSent_appliesOnlyWithMatchingClaimToken() {
        OutboxEvent event = persistEvent(OutboxEventStatus.PROCESSING, OffsetDateTime.now());
        UUID claimToken = UUID.randomUUID();
        event.setClaimToken(claimToken);
        outboxEventRepository.save(event);

        int updatedWithWrongToken = outboxEventRepository.markSent(event.getId(), UUID.randomUUID(), OffsetDateTime.now());
        int updatedWithRightToken = outboxEventRepository.markSent(event.getId(), claimToken, OffsetDateTime.now());

        assertThat(updatedWithWrongToken).isZero();
        assertThat(updatedWithRightToken).isEqualTo(1);

        OutboxEvent reloaded = outboxEventRepository.findById(event.getId()).orElseThrow();
        assertThat(reloaded.getStatus()).isEqualTo(OutboxEventStatus.SENT);
        assertThat(reloaded.getClaimToken()).isNull();
    }

    @Test
    @DisplayName("markForRetry обновляет запись только при совпадении claimToken")
    void markForRetry_appliesOnlyWithMatchingClaimToken() {
        OutboxEvent event = persistEvent(OutboxEventStatus.PROCESSING, OffsetDateTime.now());
        UUID claimToken = UUID.randomUUID();
        event.setClaimToken(claimToken);
        outboxEventRepository.save(event);

        OffsetDateTime nextAttemptAt = OffsetDateTime.now().plus(Duration.ofSeconds(30));
        int updatedWithWrongToken = outboxEventRepository.markForRetry(event.getId(), UUID.randomUUID(), 1, nextAttemptAt);
        int updatedWithRightToken = outboxEventRepository.markForRetry(event.getId(), claimToken, 1, nextAttemptAt);

        assertThat(updatedWithWrongToken).isZero();
        assertThat(updatedWithRightToken).isEqualTo(1);

        OutboxEvent reloaded = outboxEventRepository.findById(event.getId()).orElseThrow();
        assertThat(reloaded.getStatus()).isEqualTo(OutboxEventStatus.NEW);
        assertThat(reloaded.getAttempts()).isEqualTo(1);
        assertThat(reloaded.getClaimToken()).isNull();
        assertThat(reloaded.getClaimedAt()).isNull();
    }

    @Test
    @DisplayName("markFailed обновляет запись только при совпадении claimToken")
    void markFailed_appliesOnlyWithMatchingClaimToken() {
        OutboxEvent event = persistEvent(OutboxEventStatus.PROCESSING, OffsetDateTime.now());
        UUID claimToken = UUID.randomUUID();
        event.setClaimToken(claimToken);
        outboxEventRepository.save(event);

        int updatedWithWrongToken = outboxEventRepository.markFailed(event.getId(), UUID.randomUUID(), 5);
        int updatedWithRightToken = outboxEventRepository.markFailed(event.getId(), claimToken, 5);

        assertThat(updatedWithWrongToken).isZero();
        assertThat(updatedWithRightToken).isEqualTo(1);

        OutboxEvent reloaded = outboxEventRepository.findById(event.getId()).orElseThrow();
        assertThat(reloaded.getStatus()).isEqualTo(OutboxEventStatus.FAILED);
        assertThat(reloaded.getAttempts()).isEqualTo(5);
        assertThat(reloaded.getClaimToken()).isNull();
    }
}
