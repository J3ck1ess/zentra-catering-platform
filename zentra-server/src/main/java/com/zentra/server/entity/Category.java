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
    private Integer sort;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
