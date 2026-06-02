package ru.anton.springtest.repository;

import jakarta.annotation.Nonnull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.anton.springtest.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    @Nonnull
    @Override
    @EntityGraph(attributePaths = {"orders"})
    Page<User> findAll(@Nonnull Pageable pageable);

    @Nonnull
    @Override
    @EntityGraph(attributePaths = {"orders"})
    Optional<User> findById(@Nonnull UUID id);
}
