package com.zentra.server.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * User entity representing customer accounts
 */
@Data
public class User {

    private Long id;
    private Long merchantId;
    private String username;
    private String password;
    private String nickname;
    private String phone;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
