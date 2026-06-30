package ru.anton.springtest.repository;

import jakarta.annotation.Nonnull;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import ru.anton.springtest.model.Delivery;

import java.util.Optional;
import java.util.UUID;

public interface DeliveryRepository extends JpaRepository<Delivery, UUID> {

    @Override
    @Nonnull
    @EntityGraph(attributePaths = {"details"})
    Page<Delivery> findAll(@Nonnull Pageable pageable);

    @Override
    @Nonnull
    @EntityGraph(attributePaths = {"details"})
    Optional<Delivery> findById(@Nonnull UUID id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Page<Delivery> findWithLockByIsDeletedFalse(Pageable pageable);
}
