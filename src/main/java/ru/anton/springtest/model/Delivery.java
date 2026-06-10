package ru.anton.springtest.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@Setter
@Table(name = "deliveries", schema = "spring_test")
@SQLRestriction("is_deleted = false")
public class Delivery extends BaseEntity {

    private String address;

    private String status;

    @OneToOne(mappedBy = "delivery", cascade = CascadeType.ALL, orphanRemoval = true)
    private DeliveryDetails details;
}
