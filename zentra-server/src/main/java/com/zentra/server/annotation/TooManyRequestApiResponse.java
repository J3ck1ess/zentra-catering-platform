package com.zentra.server.annotation;

import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.*;

/**
 * OpenAPI rate limit response
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ApiResponse(
        responseCode = "429",
        description = "Too many requests"
)

public @interface TooManyRequestApiResponse {
}
