package ru.anton.springtest.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import ru.anton.springtest.model.Delivery;
import ru.anton.springtest.repository.DeliveryRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;

    public Delivery create(Delivery delivery) {
        return deliveryRepository.save(delivery);
    }

    public Delivery findById(UUID id) {
        return deliveryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Delivery " + id + " not found!"));
    }

    public List<Delivery> findAll() {
        return deliveryRepository.findAll();
    }

    public Delivery update(UUID id, @NonNull Delivery delivery) {
        Delivery existing = findById(id);
        existing.setAddress(delivery.getAddress());
        return deliveryRepository.save(existing);
    }

    public void deleteById(UUID id) {
        deliveryRepository.deleteById(id);
    }
}
