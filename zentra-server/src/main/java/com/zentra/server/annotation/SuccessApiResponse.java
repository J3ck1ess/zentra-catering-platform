package com.zentra.server.annotation;

import com.zentra.server.dto.BaseResponseDTO;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.*;

/**
 * OpenAPI success response
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ApiResponse(
        responseCode = "200",
        description = "Operation successful",
        content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                        implementation = BaseResponseDTO.class
                )
        )
)

public @interface SuccessApiResponse {
}
