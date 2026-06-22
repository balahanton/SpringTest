package ru.anton.springtest.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.anton.springtest.model.Product;
import ru.anton.springtest.repository.ProductRepository;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public List<Product> findAllByIds(List<UUID> ids) {
        return productRepository.findAllById(ids);
    }

    @Transactional
    public void saveAll(List<Product> products) {
        productRepository.saveAll(products);
    }
}
