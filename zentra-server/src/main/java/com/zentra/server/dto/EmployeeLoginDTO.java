package com.zentra.server.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO for login request
 */
public class EmployeeLoginDTO {

    @NotBlank
    private String username;

    @NotBlank
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
