package com.order.example.order.controller;

import com.order.example.order.dto.ItemResponse;
import com.order.example.order.dto.OrderResponse;
import com.order.example.order.exception.OrderNotFoundException;
import com.order.example.order.model.Item;
import com.order.example.order.model.Order;
import com.order.example.order.repository.OrderRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderRepository orderRepository;

    public OrderController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAll() {
        List<Order> orders = orderRepository.findAllWithItems();
        List<OrderResponse> response = orders.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOne(@PathVariable String orderId) {
        Order order = orderRepository.findByOrderIdWithItems(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        return ResponseEntity.ok(toResponse(order));
    }
        private OrderResponse toResponse(Order order) {
            double total = order.getItems().stream()
                .mapToDouble(i -> i.getPrice() * i.getQuantity())
                .sum();

         List<ItemResponse> items = order.getItems().stream()
            .map(this::toResponse)
            .collect(Collectors.toList());

        return new OrderResponse(order.getOrderId(), total, order.getStatus(), items);
}

    private ItemResponse toResponse(Item i) {
        return new ItemResponse(i.getItemId(), i.getName(), i.getQuantity(), i.getPrice());
    }
}