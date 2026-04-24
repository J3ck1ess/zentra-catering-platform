package com.zentra.server.interceptor;

import com.zentra.server.context.UserContext;
import com.zentra.server.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor for validating JWT token before request reaches controller
 */
public class JwtInterceptor implements HandlerInterceptor {

    /**
     * Pre-handle method to intercept incoming requests
     *
     * @param request  HTTP request
     * @param response HTTP response
     * @param handler  handler object
     * @return true if request should proceed, false otherwise
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        // Retrieve Authorization header
        String authHeader = request.getHeader("Authorization");

        // Validate header format
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid Authorization header");
        }

        // Extract token by removing "Bearer " prefix
        String token = authHeader.substring(7);

        try {
            // Parse and validate JWT token
            Claims claims = JwtUtil.parseToken(token);

            // Extract userId
            Long userId = Long.valueOf(claims.getSubject());

            // Store userId in ThreadLocal
            UserContext.setCurrentUser(userId);

        } catch (Exception e) {
            // Token is invalid or expired
            throw new RuntimeException("Invalid or expired token");
        }

        System.out.println("Authorization: " + request.getHeader("Authorization"));

        // Allow request to proceed
        return true;
    }

    /**
     * Clear ThreadLocal after request completion
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

        // Prevent memory leak
        UserContext.clear();
    }
}
