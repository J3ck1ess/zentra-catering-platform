package com.zentra.server.annotation;

import com.zentra.server.dto.EmployeeResponseDTO;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.*;

/**
 * OpenAPI employee response
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ApiResponse(
        responseCode = "200",
        description = "Employee retrieved successfully",
        content = @Content(
                schema = @Schema(
                        implementation = EmployeeResponseDTO.class
                )
        )
)

public @interface EmployeeApiResponse {
}
