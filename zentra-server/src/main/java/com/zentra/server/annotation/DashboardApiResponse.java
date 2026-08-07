package com.zentra.server.annotation;

import com.zentra.server.dto.DashboardResponseDTO;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.*;

/**
 * OpenAPI dashboard response
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ApiResponse(
        responseCode = "200",
        description = "Dashboard statistics retrieved successfully",
        content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                        implementation = DashboardResponseDTO.class
                )
        )
)
public @interface DashboardApiResponse {
}