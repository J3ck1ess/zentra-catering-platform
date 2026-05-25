package com.zentra.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO for updating current user profile
 */
@Data
@Schema(description = "User profile update request")
public class UserUpdateDTO {

    /**
     * User nickname
     */
    @NotBlank(message = "Nickname cannot be blank")
    @Schema(description = "User nickname")
    private String nickname;

    /**
     * User phone
     */
    @NotBlank(message = "Phone cannot be blank")
    @Schema(description = "User phone")
    private String phone;
}
