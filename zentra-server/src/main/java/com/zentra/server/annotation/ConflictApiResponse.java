package com.zentra.server.annotation;

import com.zentra.server.dto.ErrorResponseDTO;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.*;

/**
 * OpenAPI conflict response
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ApiResponse(
        responseCode = "400",
        description = "Response conflict",
        content = @Content(
                schema = @Schema(
                        implementation = ErrorResponseDTO.class
                ),

                examples = {
                        @ExampleObject(
                                name = "Conflict",
                                value = """
                                        {
                                          "code": "40900",
                                          "message": "Resource already exists"
                                        }
                                        """
                        )
                }
        )
)

public @interface ConflictApiResponse {
}
