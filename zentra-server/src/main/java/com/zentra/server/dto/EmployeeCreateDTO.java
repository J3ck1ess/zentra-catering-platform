package com.zentra.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for creating employee
 */
@Schema(description = "Employee creation request")
public class EmployeeCreateDTO {

    @Schema(description = "Employee username", example = "admin")
    @NotBlank(message = "username cannot be blank")
    @Size(
            min = 4,
            max = 20,
            message = "username must be between 4 and 20 characters"
    )
    private String username;

    @Schema(description = "Employee password", example = "123456")
    @NotBlank(message = "password cannot be blank")
    @Size(
            min = 6,
            max = 32,
            message = "password must be between 6 and 32 characters"
    )
    private String password;

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
            example = "SUPER_ADMIN"
    )
    private String role;

    // Getter and Setter
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
}
