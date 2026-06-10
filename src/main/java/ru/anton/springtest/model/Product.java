package ru.anton.springtest.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "products", schema = "spring_test")
@SQLRestriction("is_deleted = false")
public class Product extends BaseEntity {

    private String title;

    private BigDecimal price;

    @ManyToMany(mappedBy = "products", fetch = FetchType.LAZY)
    private List<Warehouse> warehouses = new ArrayList<>();
}
