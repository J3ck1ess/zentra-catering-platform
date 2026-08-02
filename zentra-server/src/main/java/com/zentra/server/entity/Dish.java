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
    private String categoryName;
    private Integer status;
    private Long merchantId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
