package ru.anton.springtest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.anton.springtest.model.SagaTask;

import java.util.List;
import java.util.UUID;

public interface SagaTaskRepository extends JpaRepository<SagaTask, UUID> {

    @Modifying
    @Query(value = """
            UPDATE spring_test.saga_tasks
            SET status = 'IN_PROGRESS'
            WHERE id IN (
                SELECT id FROM spring_test.saga_tasks
                WHERE status = 'PENDING' AND is_deleted = false
                ORDER BY created_at, id
                LIMIT :batchSize
                FOR UPDATE SKIP LOCKED
            )
            RETURNING *
            """, nativeQuery = true)
    List<SagaTask> claimPendingTasks(@Param("batchSize") int batchSize);

    @Modifying
    @Query(value = """
        UPDATE spring_test.saga_tasks
        SET status = 'IN_PROGRESS'
        WHERE id IN (
            SELECT id FROM spring_test.saga_tasks
            WHERE status = 'COMPENSATION_FAILED' AND is_deleted = false
            ORDER BY created_at, id
            LIMIT :batchSize
            FOR UPDATE SKIP LOCKED
        )
        RETURNING *
        """, nativeQuery = true)
    List<SagaTask> claimCompensationFailedTasks(@Param("batchSize") int batchSize);
}
