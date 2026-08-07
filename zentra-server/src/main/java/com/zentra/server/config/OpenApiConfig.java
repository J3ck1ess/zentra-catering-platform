package com.zentra.server.config;

import com.zentra.server.dto.BaseResponseDTO;
import com.zentra.server.dto.ErrorResponseDTO;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

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
                        .name("Authorization")
                        .description(
                                """
                                JWT Bearer token authentication.
                        
                                Format:
                                Bearer <your-jwt-token>
                        
                                RBAC-protected admin APIs require
                                corresponding permission authorization.
                                """
                        );

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
                                .description(
                                        """
                                        Enterprise-style Catering SaaS backend system with JWT authentication
                                        and RBAC-protected admin APIs.
                                        
                                        Authentication:
                                        - User APIs require JWT bearer token authentication
                                        - Admin APIs require JWT bearer token with RBAC authorization
                                        
                                        Authorization:
                                        - RBAC permissions are enforced through annotation-driven authorization
                                        - Admin APIs require corresponding permission grants
                                        """
                                )
                                .version("1.0.0")
                                .termsOfService("https://zentra.com/terms")
                                .contact(
                                        new Contact()
                                                .name("Zentra Backend Team")
                                                .email("backend@zentra.com")
                                )
                                .license(
                                        new License()
                                                .name("MIT License")
                                                .url("https://opensource.org/licenses/MIT")
                                )
                )

                // API servers
                .servers(
                        List.of(
                                new Server()
                                        .url("http://localhost:8080")
                                        .description("Local development server")
                        )
                )

                // Global security requirement
                .addSecurityItem(
                        new SecurityRequirement()
                                .addList("bearerAuth")
                )

                // External documentation
                .externalDocs(
                        new ExternalDocumentation()
                                .description("Project GitHub Repository")
                                .url("https://github.com/J3ck1ess/zentra-catering-platform")
                )

                // Reusable OpenAPI components
                .components(
                        new Components()

                                // Security schemes
                                .addSecuritySchemes(
                                        "bearerAuth",
                                        securityScheme
                                )

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
                                                "Unauthorized - JWT token is missing, invalid, or expired",
                                                errorResponseSchema,
                                                """
                                                        {
                                                          "code": 40100,
                                                          "msg": "Unauthorized access"
                                                        }
                                                        """
                                        )
                                )

                                // Forbidden response
                                .addResponses(
                                        "ForbiddenResponse",
                                        buildErrorResponse(
                                                "Forbidden - RBAC permission denied",
                                                errorResponseSchema,
                                                """
                                                        {
                                                          "code": 40300,
                                                          "msg": "No permission"
                                                        }
                                                        """
                                        )
                                )

                                // Validation error response
                                .addResponses(
                                        "ValidationErrorResponse",
                                        buildErrorResponse(
                                                "Validation failed",
                                                errorResponseSchema,
                                                """
                                                        {
                                                          "code": 40000,
                                                          "msg": "Invalid request parameters"
                                                        }
                                                        """
                                        )
                                )

                                // Resource not found response
                                .addResponses(
                                        "NotFoundResponse",
                                        buildErrorResponse(
                                                "Requested resource was not found",
                                                errorResponseSchema,
                                                """
                                                        {
                                                          "code": 40400,
                                                          "msg": "Resource not found"
                                                        }
                                                        """
                                        )
                                )

                                // Conflict response
                                .addResponses(
                                        "ConflictResponse",
                                        buildErrorResponse(
                                                "Resource conflict",
                                                errorResponseSchema,
                                                """
                                                        {
                                                          "code": 40900,
                                                          "msg": "Resource already exists"
                                                        }
                                                        """
                                        )
                                )

                                .addResponses(
                                        "DependencyConflictResponse",
                                        buildErrorResponse(
                                                "Resource dependency conflict",
                                                errorResponseSchema,
                                                """
                                                        {
                                                          "code": 40901,
                                                          "msg": "Resource is referenced by other resources"
                                                        }
                                                        """
                                        )

                                )

                                .addParameters(
                                        "PageParameter",
                                        new Parameter()
                                                .in("query")
                                                .name("page")
                                                .description("Page number")
                                                .example(1)
                                )

                                .addParameters(
                                        "PageSizeParameter",
                                        new Parameter()
                                                .in("query")
                                                .name("pageSize")
                                                .description("Page size")
                                                .example(10)
                                )
                );
    }

    /**
     * User API group
     */
    @Bean
    public GroupedOpenApi userApiGroup() {

        return GroupedOpenApi.builder()
                .group("user-api")
                .pathsToMatch(
                        "/user/**"
                )
                .build();
    }

    /**
     * Admin API group
     */
    @Bean
    public GroupedOpenApi adminApiGroup() {

        return GroupedOpenApi.builder()
                .group("admin-api")
                .pathsToMatch(
                        "/employee/**",
                        "/category/**",
                        "/dish/**",
                        "/order/**",
                        "/dashboard/**",
                        "/admin/users/**"
                )
                .build();
    }

    /**
     * Build reusable error response
     */
    private ApiResponse buildErrorResponse(
            String description,
            Schema<?> schema,
            String example
    ) {

        return new ApiResponse()
                .description(description)
                .content(
                        new Content().addMediaType(
                                "application/json",
                                new MediaType()
                                        .schema(schema)
                                        .example(example)
                        )
                );
    }
}
