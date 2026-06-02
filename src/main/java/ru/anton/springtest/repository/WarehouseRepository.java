package ru.anton.springtest.repository;

import jakarta.annotation.Nonnull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
