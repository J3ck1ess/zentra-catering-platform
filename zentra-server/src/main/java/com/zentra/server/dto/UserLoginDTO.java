package com.zentra.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO for user login
 */
@Schema(description = "User login request")
public class UserLoginDTO {

    @Schema(description = "Username", example = "sultan_bek")
    @NotBlank(message = "username cannot be blank")
    private String username;

    @Schema(description = "Password", example = "123456")
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
