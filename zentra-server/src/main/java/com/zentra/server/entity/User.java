package com.zentra.server.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * User entity representing customers
 */
@Data
public class User {

    private Long id;
    private String username;
    private String password;
    private LocalDateTime createdAt;
}
