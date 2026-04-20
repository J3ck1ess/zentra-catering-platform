package com.zentra.server.config;

import com.zentra.server.interceptor.JwtInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web configuration for registering interceptors
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Register custom interceptors
     *
     * @param registry interceptor registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(new JwtInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/employee/login/**"); // Exclude login endpoint
    }
}
