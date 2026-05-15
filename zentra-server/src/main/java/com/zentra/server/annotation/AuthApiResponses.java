package com.zentra.server.annotation;

import com.zentra.server.dto.ErrorResponseDTO;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.*;

/**
 * Common OpenAPI responses for authenticated APIs
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ApiResponses({

        @ApiResponse(
                responseCode = "401",
                description = "Invalid or expired token",
                content = @Content(
                        schema = @Schema(
                                implementation = ErrorResponseDTO.class
                        )
                )
        ),

        @ApiResponse(
                responseCode = "403",
                description = "No permission to access this API",
                content = @Content(
                        schema = @Schema(
                                implementation = ErrorResponseDTO.class
                        )
                )

        )

})

public @interface AuthApiResponses {
}
