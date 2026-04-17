package com.zentra.server.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Order entity representing customer orders
 */
@Data
public class Order {

    private Long id;
    private Long merchantId;
    private Long userId;
    private BigDecimal totalAmount;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
