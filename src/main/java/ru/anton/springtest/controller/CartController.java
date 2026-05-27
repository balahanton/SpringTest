package ru.anton.springtest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.anton.springtest.api.CartsApi;
import ru.anton.springtest.dto.CartRequest;
import ru.anton.springtest.dto.CartResponse;
import ru.anton.springtest.dto.CategoryResponse;
import ru.anton.springtest.dto.ProductResponse;
import ru.anton.springtest.model.Cart;
import ru.anton.springtest.model.Product;
import ru.anton.springtest.service.CartService;
import ru.anton.springtest.service.ProductService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CartController implements CartsApi {

    private final CartService cartService;
    private final ProductService productService;

    @Override
    public ResponseEntity<CartResponse> createCart(CartRequest request) {
        List<Product> products = request.getProductIds().stream()
                .map(productService::findById)
                .toList();

        Cart cart = new Cart();
        cart.setProducts(products);

        Cart saved = cartService.create(cart);

        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(saved));
    }

    @Override
    public ResponseEntity<List<CartResponse>> getAllCarts() {
        List<CartResponse> cartResponseList = cartService.findAll().stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(cartResponseList);
    }

    @Override
    public ResponseEntity<CartResponse> getCartById(UUID id) {
        return ResponseEntity.ok(toResponse(cartService.findById(id)));
    }

    @Override
    public ResponseEntity<Void> deleteCart(UUID id) {
        cartService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private CartResponse toResponse(Cart cart) {
        List<ProductResponse> productResponses = cart.getProducts().stream()
                .map(product -> {
                    CategoryResponse categoryResponse = new CategoryResponse();
                    categoryResponse.setId(product.getCategory().getId());
                    categoryResponse.setName(product.getCategory().getName());

                    ProductResponse productResponse = new ProductResponse();
                    productResponse.setId(product.getId());
                    productResponse.setName(product.getName());
                    productResponse.setPrice(product.getPrice());
                    productResponse.setCategory(categoryResponse);
                    return productResponse;
                })
                .toList();

        CartResponse response = new CartResponse();
        response.setId(cart.getId());
        response.setProducts(productResponses);
        return response;
    }
}
