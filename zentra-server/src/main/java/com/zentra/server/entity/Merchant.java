package com.zentra.server.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Merchant entity representing a tenant in SaaS system
 */
@Data
public class Merchant {

    private Long id;
    private String name;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
