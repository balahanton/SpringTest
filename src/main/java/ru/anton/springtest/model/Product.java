package ru.anton.springtest.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "products", schema = "spring_test")
@SQLRestriction("is_deleted = false")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    private String title;

    private BigDecimal price;

    @ManyToMany(mappedBy = "products", fetch = FetchType.LAZY)
    private List<Warehouse> warehouses = new ArrayList<>();
}
