package ru.anton.springtest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
}
