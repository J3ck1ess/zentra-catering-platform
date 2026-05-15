package com.zentra.server.annotation;

import com.zentra.server.dto.ErrorResponseDTO;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.*;

/**
 * OpenAPI not found response
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ApiResponse(
        responseCode = "400",
        description = "Resource not Found",
        content = @Content(
                schema = @Schema(
                        implementation = ErrorResponseDTO.class
                ),

                examples = {
                        @ExampleObject(
                                name = "Not found",
                                value = """
                                        {
                                          "code" : 40400,
                                          "msg": "Resource not found"
                                        }
                                        """
                        )
                }
        )
)

public @interface NotFoundApiResponse {
}
