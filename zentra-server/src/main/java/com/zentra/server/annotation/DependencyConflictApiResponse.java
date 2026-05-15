package com.zentra.server.annotation;

import com.zentra.server.dto.ErrorResponseDTO;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.*;

/**
 * OpenAPI dependency conflict response
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ApiResponse(
        responseCode = "400",
        description = "Resource dependency conflict",
        content = @Content(
                schema = @Schema(
                        implementation = ErrorResponseDTO.class
                ),

                examples = {
                        @ExampleObject(
                                name = "Dependency Conflict",
                                value = """
                                        {
                                          "code": "40901",
                                          "message": "Resource is referenced by other resources"
                                        }
                                        """
                        )
                }
        )
)

public @interface DependencyConflictApiResponse {
}
