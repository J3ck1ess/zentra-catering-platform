package com.zentra.server.annotation;

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
                ref = "#/components/responses/UnauthorizedResponse"
        ),

        @ApiResponse(
                ref = "#/components/responses/ForbiddenResponse"

        )

})

public @interface AuthApiResponses {
}
