package ru.anton.springtest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.anton.springtest.model.Product;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
}
