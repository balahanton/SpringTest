package ru.anton.springtest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.anton.springtest.api.DeliveriesApi;
import ru.anton.springtest.dto.DeliveryRequest;
import ru.anton.springtest.dto.DeliveryResponse;
import ru.anton.springtest.model.Delivery;
import ru.anton.springtest.service.DeliveryService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class DeliveryController implements DeliveriesApi {

    private final DeliveryService deliveryService;

    @Override
    public ResponseEntity<DeliveryResponse> createDelivery(DeliveryRequest request) {
        Delivery delivery = new Delivery();
        delivery.setAddress(request.getAddress());

        Delivery saved = deliveryService.create(delivery);

        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(saved));
    }

    @Override
    public ResponseEntity<List<DeliveryResponse>> getAllDeliveries() {
        List<DeliveryResponse> deliveryResponseList = deliveryService.findAll().stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(deliveryResponseList);
    }

    @Override
    public ResponseEntity<DeliveryResponse> getDeliveryById(UUID id) {
        return ResponseEntity.ok(toResponse(deliveryService.findById(id)));
    }

    @Override
    public ResponseEntity<DeliveryResponse> updateDelivery(UUID id, DeliveryRequest request) {
        Delivery delivery = new Delivery();
        delivery.setAddress(request.getAddress());

        Delivery updated = deliveryService.update(id, delivery);

        return ResponseEntity.ok(toResponse(updated));
    }

    @Override
    public ResponseEntity<Void> deleteDelivery(UUID id) {
        deliveryService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private DeliveryResponse toResponse(Delivery delivery) {
        DeliveryResponse response = new DeliveryResponse();
        response.setId(delivery.getId());
        response.setAddress(delivery.getAddress());
        return response;
    }
}