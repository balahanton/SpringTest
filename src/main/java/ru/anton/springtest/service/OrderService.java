package ru.anton.springtest.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.anton.springtest.model.Order;
import ru.anton.springtest.repository.OrderRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public Order create(Order order) {
        return orderRepository.save(order);
    }

    public Order findById(UUID id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
    }

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public void delete(UUID id) {
        orderRepository.deleteById(id);
    }
}
