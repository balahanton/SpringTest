package ru.anton.springtest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import ru.anton.springtest.api.OrdersApi;
import ru.anton.springtest.dto.DeliveryResponse;
import ru.anton.springtest.dto.OrderRequest;
import ru.anton.springtest.dto.OrderResponse;
import ru.anton.springtest.dto.UserResponse;
import ru.anton.springtest.model.Delivery;
import ru.anton.springtest.model.Order;
import ru.anton.springtest.model.User;
import ru.anton.springtest.service.OrderService;
import ru.anton.springtest.service.UserService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class OrderController implements OrdersApi {

    private final OrderService orderService;
    private final UserService userService;

    @Override
    public ResponseEntity<OrderResponse> createOrder(OrderRequest request) {
        User user = userService.findById(request.getUserId());

        Delivery delivery = new Delivery();
        delivery.setAddress(request.getDelivery().getAddress());

        Order order = new Order();
        order.setUser(user);
        order.setDelivery(delivery);
        delivery.setOrder(order);

        Order saved = orderService.create(order);

        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(saved));
    }

    @Transactional(readOnly = true)
    @Override
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        List<OrderResponse> orderResponseList = orderService.findAll().stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(orderResponseList);
    }

    @Transactional(readOnly = true)
    @Override
    public ResponseEntity<OrderResponse> getOrderById(UUID id) {
        return ResponseEntity.ok(toResponse(orderService.findById(id)));
    }

    @Override
    public ResponseEntity<Void> deleteOrder(UUID id) {
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private OrderResponse toResponse(Order order) {
        DeliveryResponse deliveryResponse = new DeliveryResponse();
        deliveryResponse.setId(order.getDelivery().getId());
        deliveryResponse.setAddress(order.getDelivery().getAddress());

        UserResponse userResponse = new UserResponse();
        userResponse.setId(order.getUser().getId());
        userResponse.setUsername(order.getUser().getUsername());

        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setUser(userResponse);
        response.setDelivery(deliveryResponse);

        return response;
    }
}
