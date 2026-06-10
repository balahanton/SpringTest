package ru.anton.springtest.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "warehouses", schema = "spring_test")
@SQLRestriction("is_deleted = false")
public class Warehouse extends BaseEntity {

    private String name;

    @ManyToMany(cascade = {CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(
            name = "warehouses_products",
            schema = "spring_test",
            joinColumns = @JoinColumn(name = "warehouse_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private List<Product> products = new ArrayList<>();
}
