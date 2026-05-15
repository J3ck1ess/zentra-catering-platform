package com.zentra.server.annotation;

import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.*;

/**
 * OpenAPI not found response
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ApiResponse(
        ref = "#/components/responses/NotFoundResponse"
)

public @interface NotFoundApiResponse {
}
