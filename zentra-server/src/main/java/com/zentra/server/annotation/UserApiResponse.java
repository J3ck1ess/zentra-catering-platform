package com.zentra.server.annotation;

import com.zentra.server.dto.UserResponseDTO;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.*;

/**
 * OpenAPI user response
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ApiResponse(
        responseCode = "200",
        description = "Operation successful",
        content = @Content(
                schema = @Schema(
                        implementation = UserResponseDTO.class
                )
        )
)

public @interface UserApiResponse {
}
