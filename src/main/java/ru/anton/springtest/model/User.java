package ru.anton.springtest.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "users", schema = "spring_test")
@SQLRestriction("is_deleted = false")
public class User extends BaseEntity {

    private String username;

    @Enumerated(EnumType.STRING)
    private SagaTaskStatus enrichmentStatus;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders;
}
