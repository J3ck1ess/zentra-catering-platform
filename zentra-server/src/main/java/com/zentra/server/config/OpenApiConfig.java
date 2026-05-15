package com.zentra.server.config;

import com.zentra.server.dto.BaseResponseDTO;
import com.zentra.server.dto.ErrorResponseDTO;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI / Swagger configuration
 */
@Configuration
public class OpenApiConfig {

    /**
     * Configure OpenAPI documentation
     */
    @Bean
    public OpenAPI zentraOpenAPI() {

        // JWT security scheme
        SecurityScheme securityScheme =
                new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .name("Authorization");

        // Global reusable schemas
        Schema<?> errorResponseSchema =
                ModelConverters.getInstance()
                        .readAllAsResolvedSchema(
                                new AnnotatedType(ErrorResponseDTO.class)
                        )
                        .schema;

        Schema<?> baseResponseSchema =
                ModelConverters.getInstance()
                        .readAllAsResolvedSchema(
                                new AnnotatedType(BaseResponseDTO.class)
                        )
                        .schema;

        return new OpenAPI()

                // API info
                .info(
                        new Info()
                                .title("Zentra Catering Platform API")
                                .description("Enterprise-style Catering Saas Backend")
                                .version("1.0.0")
                                .license(
                                        new License()
                                                .name("Open Source")
                                )
                )

                // JWT authentication
                .schemaRequirement(
                        "bearerAuth",
                        securityScheme
                )

                // Global security requirement
                .addSecurityItem(
                        new SecurityRequirement()
                                .addList("bearerAuth")
                )

                // Optional external docs
                .externalDocs(
                        new ExternalDocumentation()
                                .description("Project Documentation")
                )

                .components(
                        new Components()

                                // Global reusable schemas
                                .addSchemas(
                                        "ErrorResponse",
                                        errorResponseSchema
                                )

                                .addSchemas(
                                        "BaseResponse",
                                        baseResponseSchema
                                )

                                // Unauthorized response
                                .addResponses(
                                        "UnauthorizedResponse",
                                        buildErrorResponse(
                                                "Unauthorized - JWT token is missing or invalid",
                                                errorResponseSchema
                                        )
                                )

                                // Forbidden response
                                .addResponses(
                                        "ForbiddenResponse",
                                        buildErrorResponse(
                                                "Forbidden - Access denied",
                                                errorResponseSchema
                                        )
                                )

                                // Validation error response
                                .addResponses(
                                        "ValidationErrorResponse",
                                        buildErrorResponse(
                                                "Validation failed",
                                                errorResponseSchema
                                        )
                                )

                                // Resource not found response
                                .addResponses(
                                        "NotFoundResponse",
                                        buildErrorResponse(
                                                "Requested resource was not found",
                                                errorResponseSchema
                                        )
                                )
                );
    }

    private ApiResponse buildErrorResponse(
            String description,
            Schema<?> schema
    ) {

        return new ApiResponse()
                .description(description)
                .content(
                        new Content().addMediaType(
                                "application/json",
                                new MediaType()
                                        .schema(schema)
                        )
                );
    }
}
