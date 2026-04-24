package com.zentra.server.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Dish entity for menu items
 */
@Data
public class Dish {

    private Long id;
    private String name;
    private BigDecimal price;
    private Long categoryId;
    private Integer status; // 1. available, 2. unavailable
    private Long merchantId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
