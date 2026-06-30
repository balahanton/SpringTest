package ru.anton.springtest.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@Setter
@Table(name = "delivery_details", schema = "spring_test")
@SQLRestriction("is_deleted = false")
public class DeliveryDetails extends BaseEntity {

    @Column(name = "courier_name")
    private String courierName;

    @Column(name = "delivery_notes")
    private String deliveryNotes;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_id", nullable = false)
    private Delivery delivery;
}
