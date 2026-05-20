package com.zentra.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO for updating employee
 */
@Schema(description = "Employee update request")
public class EmployeeUpdateDTO {

    @Schema(description = "Employee ID", example = "1")
    @NotNull(message = "employee id cannot be null")
    private Long id;

    @Schema(description = "Employee username", example = "admin")
    @Size(
            min = 4,
            max = 20,
            message = "username must be between 4 and 20 characters"
    )
    private String username;

    @Schema(description = "Employee display name", example = "Admin User")
    @Size(
            max = 50,
            message = "name must be less than 50 characters"
    )
    private String name;

    @Schema(
            description = "Employee role",
            allowableValues = {
                    "SUPER_ADMIN",
                    "STORE_MANAGER",
                    "CASHIER",
                    "KITCHEN_STAFF"
            },
            example = "STORE_MANAGER"
    )
    private String role;

    @Schema(description = "Employee status (1 = Active, 0 = Disabled)", example = "1")
    private Integer status;

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
}
