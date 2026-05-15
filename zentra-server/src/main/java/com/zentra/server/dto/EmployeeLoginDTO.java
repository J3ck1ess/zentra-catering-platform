package com.zentra.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO for login request
 */
@Schema(description = "Employee login request")
public class EmployeeLoginDTO {

    @Schema(description = "Employee username", example = "admin")
    @NotBlank(message = "username cannot be blank")
    private String username;

    @Schema(description = "Employee password", example = "123456")
    @NotBlank(message = "password cannot be blank")
    private String password;

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
}
