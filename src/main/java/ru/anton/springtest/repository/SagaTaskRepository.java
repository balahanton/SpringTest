package ru.anton.springtest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.anton.springtest.model.SagaTask;
import ru.anton.springtest.model.SagaTaskStatus;

import java.util.List;
import java.util.UUID;

public interface SagaTaskRepository extends JpaRepository<SagaTask, UUID> {

    List<SagaTask> findByStatus(SagaTaskStatus status);
}
