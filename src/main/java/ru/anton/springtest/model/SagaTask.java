package ru.anton.springtest.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "saga_tasks", schema = "spring_test")
@SQLRestriction("is_deleted = false")
public class SagaTask extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SagaTaskStatus status = SagaTaskStatus.PENDING;

    @Column(nullable = false)
    private String discountCardNumber;
    @Column(nullable = false)
    private BigDecimal balance;

    @Column(nullable = false)
    private Integer attempts = 0;
}
