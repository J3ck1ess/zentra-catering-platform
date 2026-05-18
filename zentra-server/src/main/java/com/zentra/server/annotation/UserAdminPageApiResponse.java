package com.zentra.server.annotation;

import com.zentra.server.dto.UserAdminPageResponseDTO;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.*;

/**
 * OpenAPI response for paginated admin user query
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ApiResponse(
        responseCode = "200",
        description = "Users retrieved successfully",
        content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                        implementation = UserAdminPageResponseDTO.class
                )
        )
)

public @interface UserAdminPageApiResponse {
}
