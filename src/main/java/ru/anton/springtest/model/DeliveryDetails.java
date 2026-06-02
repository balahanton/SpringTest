package ru.anton.springtest.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "delivery_details", schema = "spring_test")
@SQLDelete(sql = "UPDATE spring_test.delivery_details SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = false")
public class DeliveryDetails extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "courier_name")
    private String courierName;

    @Column(name = "delivery_notes")
    private String deliveryNotes;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_id", nullable = false)
    private Delivery delivery;
}
