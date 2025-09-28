package com.order.example.order.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.order.example.order.model.Order;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.slf4j.MDC;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class OrderService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Validator validator;
    private final OrderProcessingService orderProcessingService;
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    public OrderService(OrderProcessingService orderProcessingService) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
        this.orderProcessingService = orderProcessingService;
    }

    @RabbitListener(containerFactory = "listenerContainerFactory", queues = "${rabbitmq.queuename}")
    public void receiveOrders(Message message) {
        String orderJson = new String(message.getBody(), StandardCharsets.UTF_8);

        String traceId = UUID.randomUUID().toString();
        MDC.put("traceId", traceId);

        log.info("Message received: {}", orderJson);

        try {
            Order order = objectMapper.readValue(orderJson, Order.class);

            Set<ConstraintViolation<Order>> violations = validator.validate(order);
            if (!violations.isEmpty()) {
                log.error("Validation error for orderId={}", order.getOrderId());
                violations.forEach(v -> log.warn(" - {}: {}", v.getPropertyPath(), v.getMessage()));

                orderProcessingService.handleError(order);
                return;
            }

            orderProcessingService.saveOrder(order);
            log.info("Order saved successfully: {}", order.getOrderId());

        } catch (Exception e) {
            log.error("Error processing message: {}", e.getMessage(), e);
            orderProcessingService.handleException(orderJson);
        }
    }
}