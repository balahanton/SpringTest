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
    private SagaTaskStatus status;

    private String discountCardNumber;
    private BigDecimal balance;

    private Integer attempts = 0;
}
