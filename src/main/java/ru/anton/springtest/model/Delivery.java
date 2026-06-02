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
@Table(name = "deliveries", schema = "spring_test")
@SQLDelete(sql = "UPDATE spring_test.deliveries SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = false")
public class Delivery extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String address;

    private String status;

    @OneToOne(mappedBy = "delivery", cascade = CascadeType.ALL, orphanRemoval = true)
    private DeliveryDetails details;
}
