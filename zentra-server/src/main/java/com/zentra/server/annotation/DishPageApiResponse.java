package com.zentra.server.annotation;

import com.zentra.server.dto.DishPageResponseDTO;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.*;

/**
 * OpenAPI dish page response
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ApiResponse(
        responseCode = "200",
        description = "Dishes retrieved successfully",
        content = @Content(
                schema = @Schema(
                        implementation = DishPageResponseDTO.class
                )
        )
)

public @interface DishPageApiResponse {
}
