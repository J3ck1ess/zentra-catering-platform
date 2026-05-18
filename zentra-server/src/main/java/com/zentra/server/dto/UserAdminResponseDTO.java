package com.zentra.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Admin user API response
 */
@Schema(description = "Admin user API response")
public class UserAdminResponseDTO extends BaseResponseDTO {

    @Schema(description = "Admin user response data")
    private UserAdminDTO data;

    // Getter and Setter
    public UserAdminDTO getData() {
        return data;
    }

    public void setData(UserAdminDTO data) {
        this.data = data;
    }
}
