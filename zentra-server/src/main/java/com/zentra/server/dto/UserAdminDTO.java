package com.zentra.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * DTO for admin user response
 */
@Schema(description = "Admin user response")
public class UserAdminDTO {

    @Schema(description = "User ID", example = "1")
    private Long id;

    @Schema(description = "Username", example = "sultan")
    private String username;

    @Schema(description = "User status (1 = Active, 0 = Disabled)", example = "1")
    private Integer status;

    @Schema(description = "User creation time", example = "2026-05-07T18:50:56")
    private LocalDateTime createdAt;

    // Getter and Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
