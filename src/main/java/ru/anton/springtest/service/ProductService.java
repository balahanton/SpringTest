package ru.anton.springtest.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.anton.springtest.exception.EntityNotFoundException;
import ru.anton.springtest.model.Product;
import ru.anton.springtest.repository.ProductRepository;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    public Product findByIdOrThrow(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Товар с ID {} не найден", id);
                    return new EntityNotFoundException("Product with ID " + id + " not found");
                });
    }
}
