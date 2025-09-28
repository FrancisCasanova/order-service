package com.order.example.order.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class ItemResponse {
    private String itemId;
    private String name;
    private int quantity;
    private double price;

}