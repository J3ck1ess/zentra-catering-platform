package com.zentra.server.interceptor;

import com.zentra.common.auth.AuthInfo;
import com.zentra.common.context.AuthContext;
import com.zentra.common.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT authentication interceptor
 */
@Component
public class JwtTokenInterceptor implements HandlerInterceptor {

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
