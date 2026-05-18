package com.zentra.server.annotation;

import com.zentra.server.dto.LoginResponseWrapperDTO;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.*;

/**
 * OpenAPI login response
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ApiResponse(
        responseCode = "200",
        description = "Login successful",
        content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                        implementation = LoginResponseWrapperDTO.class
                )
        )
)

public @interface LoginApiResponse {
}
