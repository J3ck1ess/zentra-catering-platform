package com.zentra.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * User API response
 */
@Schema(description = "User API response")
public class UserResponseDTO extends BaseResponseDTO{

    @Schema(description = "User response data")
    private UserDTO data;

    // Getter and Setter
    public UserDTO getData() {
        return data;
    }

    public void setData(UserDTO data) {
        this.data = data;
    }
}
