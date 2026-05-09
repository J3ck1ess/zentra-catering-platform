package com.zentra.server.interceptor;

import com.zentra.common.auth.AuthInfo;
import com.zentra.common.constant.UserType;
import com.zentra.common.context.AuthContext;
import com.zentra.common.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;

/**
 * JWT authentication interceptor
 */
@Component
public class JwtTokenInterceptor implements HandlerInterceptor {

    /**
     * Employee-only API prefixes
     */
    private static final List<String> EMPLOYEE_APIS =
            List.of(
                    "/employee",
                    "/category",
                    "/dish",
                    "/order"
            );

    /**
     * User-only API prefixes
     */
    private static final List<String> USER_APIS =
            List.of(
                    "/user"
            );

    /**
     * Execute before controller
     */
    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) {

        // Retrieve Authorization header
        String authHeader = request.getHeader("Authorization");

        // Validate header format
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid Authorization header");
        }

        // Extract token by removing "Bearer " prefix
        String token = authHeader.substring(7);

        // Parse JWT
        AuthInfo authInfo = JwtUtil.parseToken(token);

        // Current request URI
        String requestUri = request.getRequestURI();

        // Current login user type
        String userType = authInfo.getUserType();

        // EMPLOYEE can only access employee APIs
        if (UserType.EMPLOYEE.equals(userType)) {

            boolean allowed = EMPLOYEE_APIS.stream()
                    .anyMatch(requestUri::startsWith);

            if (!allowed) {
                throw new IllegalArgumentException(
                        "No permission to access this API"
                );
            }
        }

        // USER can only access user APIs
        if (UserType.USER.equals(userType)) {

            boolean allowed = USER_APIS.stream()
                    .anyMatch(requestUri::startsWith);

            if (!allowed) {
                throw new IllegalArgumentException(
                        "No permission to access this API"
                );
            }
        }

        // Store authentication info in ThreadLocal
        AuthContext.setCurrentUserId(authInfo.getUserId());

        AuthContext.setCurrentMerchantId(authInfo.getMerchantId());

        AuthContext.setCurrentUserType(authInfo.getUserType());

        return true;
    }

    /**
     * Clear ThreadLocal after request completion
     */
    @Override
    public void afterCompletion(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            Exception ex
    ) {

        AuthContext.clear();
    }
}
