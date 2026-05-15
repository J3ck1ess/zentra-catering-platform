package com.zentra.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response DTO for login API
 */
@Schema(description = "Login response")
public class LoginResponse {

    @Schema(description = "JWT access token", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String token;

    @Schema(description = "Current authenticated user ID", example = "1")
    private Long userId;

    public LoginResponse(String token, Long userId) {
        this.token = token;
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public Long getUserId() {
        return userId;
    }
}
