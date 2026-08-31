package ru.anton.springtest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.anton.springtest.model.OutboxEvent;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {

    @Query(value = """
        SELECT * FROM spring_test.outbox_events
        WHERE (status = 'NEW' AND (next_attempt_at IS NULL OR next_attempt_at <= now()))
           OR (status = 'PROCESSING' AND claimed_at < :staleBefore)
        ORDER BY created_at
        LIMIT :batchSize
        FOR UPDATE SKIP LOCKED
        """, nativeQuery = true)
    List<OutboxEvent> claimPendingEvents(int batchSize, OffsetDateTime staleBefore);

    @Transactional
    @Modifying
    @Query(value = """
        UPDATE spring_test.outbox_events
        SET status = 'SENT', sent_at = :sentAt, claim_token = NULL
        WHERE id = :id AND claim_token = :claimToken
        """, nativeQuery = true)
    int markSent(UUID id, UUID claimToken, OffsetDateTime sentAt);

    @Transactional
    @Modifying
    @Query(value = """
        UPDATE spring_test.outbox_events
        SET status = 'FAILED', attempts = :attempts, claimed_at = NULL, claim_token = NULL
        WHERE id = :id AND claim_token = :claimToken
        """, nativeQuery = true)
    int markFailed(UUID id, UUID claimToken, int attempts);

    @Transactional
    @Modifying
    @Query(value = """
        UPDATE spring_test.outbox_events
        SET status = 'NEW', attempts = :attempts, next_attempt_at = :nextAttemptAt, claimed_at = NULL, claim_token = NULL
        WHERE id = :id AND claim_token = :claimToken
        """, nativeQuery = true)
    int markForRetry(UUID id, UUID claimToken, int attempts, OffsetDateTime nextAttemptAt);
}
