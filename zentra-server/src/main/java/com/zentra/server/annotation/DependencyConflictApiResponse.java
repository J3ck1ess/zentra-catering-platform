package com.zentra.server.annotation;

import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.*;

/**
 * OpenAPI dependency conflict response
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ApiResponse(
        ref = "#/components/responses/DependencyConflictResponse"
)

public @interface DependencyConflictApiResponse {
}
