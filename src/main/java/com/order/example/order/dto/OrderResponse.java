package com.order.example.order.dto;

import com.order.example.order.Enum.OrderStatus;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class OrderResponse {
    private String orderId;
    private double total;
    private OrderStatus status;
    private List<ItemResponse> items;
}