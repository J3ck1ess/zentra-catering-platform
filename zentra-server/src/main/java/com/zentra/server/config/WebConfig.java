package com.zentra.server.config;

import com.zentra.server.interceptor.JwtTokenInterceptor;
import com.zentra.server.interceptor.PermissionInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web configuration for registering interceptors
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final JwtTokenInterceptor jwtTokenInterceptor;

    private final PermissionInterceptor permissionInterceptor;

    public WebConfig(
            JwtTokenInterceptor jwtTokenInterceptor,
            PermissionInterceptor permissionInterceptor
    ) {

        this.jwtTokenInterceptor = jwtTokenInterceptor;
        this.permissionInterceptor = permissionInterceptor;
    }

    /**
     * Register custom interceptors
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        // JWT authentication interceptor
        registry.addInterceptor(jwtTokenInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(

                        // Authentication APIs
                        "/employee/login",
                        "/user/register",
                        "/user/login",

                        // Swagger/OpenAPI
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/swagger-resources/**",
                        "/webjars/**"

                );

        // RBAC permission interceptor
        registry.addInterceptor(permissionInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(

                        // Authentication APIs
                        "/employee/login",
                        "/user/register",
                        "/user/login",

                        // Swagger/OpenAPI
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/swagger-resources/**",
                        "/webjars/**"
                );
    }
}
