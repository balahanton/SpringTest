package ru.anton.springtest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.anton.springtest.api.ProductsApi;
import ru.anton.springtest.dto.CategoryResponse;
import ru.anton.springtest.dto.ProductRequest;
import ru.anton.springtest.dto.ProductResponse;
import ru.anton.springtest.model.Category;
import ru.anton.springtest.model.Product;
import ru.anton.springtest.service.CategoryService;
import ru.anton.springtest.service.ProductService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ProductController implements ProductsApi {

    private final ProductService productService;
    private final CategoryService categoryService;

    @Override
    public ResponseEntity<ProductResponse> createProduct(ProductRequest request) {
        Category category = categoryService.findById(request.getCategoryId());

        Product product = new Product();
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setCategory(category);

        Product saved = productService.create(product);

        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(saved));
    }

    @Override
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
       List<ProductResponse> productResponseList = productService.findAll().stream()
               .map(this::toResponse)
               .toList();
       return ResponseEntity.ok(productResponseList);
    }

    @Override
    public ResponseEntity<ProductResponse> getProductById(UUID uuid) {
        return ResponseEntity.ok(toResponse(productService.findById(uuid)));
    }

    @Override
    public ResponseEntity<ProductResponse> updateProduct(UUID id, ProductRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setPrice(request.getPrice());

        Product updated = productService.update(id, product);

        return ResponseEntity.ok(toResponse(updated));
    }

    @Override
    public ResponseEntity<Void> deleteProduct(UUID id) {
        productService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private ProductResponse toResponse(Product product) {
        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setId(product.getCategory().getId());
        categoryResponse.setName(product.getCategory().getName());

        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setPrice(product.getPrice());
        response.setCategory(categoryResponse);

        return response;
    }
}
