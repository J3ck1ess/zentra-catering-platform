package com.zentra.server.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Order item entity representing order details
 */
@Data
public class OrderItem {

    private Long id;
    private Long orderId;
    private Long productId;
    private String name;
    private BigDecimal price;
    private Integer quantity;
}
