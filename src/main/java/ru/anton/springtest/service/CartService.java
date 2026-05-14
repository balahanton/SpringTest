package ru.anton.springtest.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.anton.springtest.model.Cart;
import ru.anton.springtest.repository.CartRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;

    public Cart create(Cart cart) {
        return cartRepository.save(cart);
    }

    public Cart findById(UUID id) {
        return cartRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cart with id: " + id + " not found!"));
    }

    public List<Cart> findAll() {
        return cartRepository.findAll();
    }

    public void deleteById(UUID id) {
        cartRepository.deleteById(id);
    }
}
