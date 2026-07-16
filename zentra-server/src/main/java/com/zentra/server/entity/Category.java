package com.zentra.server.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Category entity for product classification
 */
@Data
public class Category {

    private Long id;
    private Long merchantId;
    private String name;
    private Integer type; // 1. dish, 2. setMeal
    private Integer sort;
    private Integer status;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
