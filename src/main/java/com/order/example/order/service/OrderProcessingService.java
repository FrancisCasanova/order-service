package com.order.example.order.service;

import com.order.example.order.Enum.OrderStatus;
import com.order.example.order.model.Order;
import com.order.example.order.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class OrderProcessingService {

    private static final Logger log = LoggerFactory.getLogger(OrderProcessingService.class);

    private final OrderRepository orderRepository;

    public OrderProcessingService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    public void saveOrder(Order order) {
        log.info("Starting processing for orderId={}", order.getOrderId());

        if (orderRepository.existsByOrderId(order.getOrderId())) {
            log.error("Duplicate order detected: {}", order.getOrderId());
            throw new IllegalStateException("Duplicate order: " + order.getOrderId());
        }

        order.setStatus(OrderStatus.RECEIVED);
        log.info("Order {} marked as RECEIVED", order.getOrderId());

        try {
            double total = order.getItems().stream()
                    .mapToDouble(i -> i.getPrice() * i.getQuantity())
                    .sum();
            order.setTotal(total);

            order.setStatus(OrderStatus.PROCESSING);
            log.info("Order {} is PROCESSING (total={})", order.getOrderId(), total);
            orderRepository.save(order);

            order.setStatus(OrderStatus.PROCESSED);
            orderRepository.save(order);
            log.info("Order {} successfully PROCESSED", order.getOrderId());

        } catch (Exception e) {
            order.setStatus(OrderStatus.FAILED);
            orderRepository.save(order);
            log.error("Order {} FAILED: {}", order.getOrderId(), e.getMessage(), e);
            handleError(order);
            throw e;
        }
    }

    public void handleError(Order order) {
        log.error("Sending error notification for orderId={} (status={})", order.getOrderId(), order.getStatus());
        //Enviar mensagem de volta para o A falando que deu erro(REST, RabbitMQ...)
    }

    public void handleException(String orderJson) {
        log.error("Sending error notification for raw order message: {}", orderJson);
        //Enviar mensagem de volta para o A falando que deu erro(REST, RabbitMQ...)
    }
}
