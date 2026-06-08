package ru.anton.springtest.repository;

import jakarta.annotation.Nonnull;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import ru.anton.springtest.model.Delivery;
import ru.anton.springtest.model.Warehouse;

import java.util.Optional;
import java.util.UUID;

public interface WarehouseRepository extends JpaRepository<Warehouse, UUID> {

    @Override
    @Nonnull
    @EntityGraph(attributePaths = {"products"})
    Page<Warehouse> findAll(@Nonnull Pageable pageable);

    @Override
    @Nonnull
    @EntityGraph(attributePaths = {"products"})
    Optional<Warehouse> findById(@Nonnull UUID id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = {"details"})
    Page<Warehouse> findWithLockByIsDeletedFalse(Pageable pageable);
}
