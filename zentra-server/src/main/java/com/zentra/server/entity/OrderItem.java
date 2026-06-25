package com.zentra.server.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Order item entity representing order details
 */
@Data
public class OrderItem {

    private Long id;
    private Long merchantId;
    private Long orderId;
    private Long dishId;
    private String dishName;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal amount;
}
