package com.zentra.common.dto.verification;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Send verification code request DTO
 */
@Data
@Schema(description = "Send verification code request")
public class SendVerificationCodeRequestDTO {

    /**
     * Verification code type
     */
    @NotBlank(message = "Verification type cannot be blank")
    @Schema(
            description = "Verification code type",
            example = "LOGIN"
    )
    private String type;

    /**
     * Verification target
     */
    @NotBlank(message = "Verification target cannot be blank")
    @Schema(
            description = "Verification target such as email or phone",
            example = "test@example.com"
    )
    private String target;
}
