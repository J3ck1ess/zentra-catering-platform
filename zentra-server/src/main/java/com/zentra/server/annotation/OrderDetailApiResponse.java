package com.zentra.server.annotation;

import com.zentra.server.dto.OrderDetailResponseDTO;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.*;

/**
 * OpenAPI order detail response
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ApiResponse(
        responseCode = "200",
        description = "Order retrieved successfully",
        content = @Content(
                schema = @Schema(
                        implementation = OrderDetailResponseDTO.class
                )
        )
)

public @interface OrderDetailApiResponse {
}
