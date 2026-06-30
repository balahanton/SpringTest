package ru.anton.springtest.repository;

import jakarta.annotation.Nonnull;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
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

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Page<User> findWithLockByIsDeletedFalse(Pageable pageable);
}
