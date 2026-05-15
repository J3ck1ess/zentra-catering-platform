package com.zentra.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Login API response
 */
@Schema(description = "Login API response")
public class LoginResponseWrapperDTO extends BaseResponseDTO{

    @Schema(description = "Login response data")
    private LoginResponse data;

    // Getter and Setter
    public LoginResponse getData() {
        return data;
    }

    public void setData(LoginResponse data) {
        this.data = data;
    }
}
