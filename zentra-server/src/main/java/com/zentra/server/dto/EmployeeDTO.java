package com.zentra.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * DTO for employee response
 */
@Schema(description = "Employee response")
public class EmployeeDTO {

    @Schema(description = "Employee ID", example = "1")
    private Long id;

    @Schema(description = "Employee username", example = "admin")
    private String username;

    @Schema(description = "Employee display name", example = "Admin User")
    private String name;

    @Schema(
            description = "Employee role",
            allowableValues = {
                    "SUPER_ADMIN",
                    "STORE_MANAGER",
                    "CASHIER",
                    "KITCHEN_STAFF"
            },
            example = "CASHIER"
    )
    private String role;

    @Schema(description = "Employee status (1 = Active, 0 = Disabled)", example = "1")
    private Integer status;

    @Schema(description = "Employee creation time", example = "2026-05-07T18:50:56")
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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
