package com.zentra.server.entity;

import lombok.Data;

import java.time.LocalDateTime;
/**
 * Employee entity representing backend staff users
 */
@Data
public class Employee {

    private Long id;
    private Long merchantId;
    private String username;
    private String password;
    private String name;
    private String role;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
