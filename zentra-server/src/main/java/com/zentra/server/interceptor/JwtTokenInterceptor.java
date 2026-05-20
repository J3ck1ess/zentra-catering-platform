package com.zentra.server.interceptor;

import com.zentra.common.auth.AuthInfo;
import com.zentra.common.constant.ErrorCode;
import com.zentra.common.constant.ErrorMessage;
import com.zentra.common.constant.UserType;
import com.zentra.common.context.AuthContext;
import com.zentra.common.context.PermissionContext;
import com.zentra.common.exception.BusinessException;
import com.zentra.common.util.JwtUtil;
import com.zentra.server.security.PermissionProvider;
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
                    "/order",
                    "/admin"
            );

    /**
     * User-only API prefixes
     */
    private static final List<String> USER_APIS =
            List.of(
                    "/user"
            );

    /**
     * RBAC permission provider
     */
    private final PermissionProvider permissionProvider;

    /**
     * Constructor injection
     */
    public JwtTokenInterceptor(
            PermissionProvider permissionProvider
    ) {

        this.permissionProvider = permissionProvider;
    }

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

            throw new BusinessException(
                    ErrorCode.TOKEN_INVALID,
                    ErrorMessage.TOKEN_INVALID
            );
        }

        // Extract token by removing "Bearer " prefix
        String token = authHeader.substring(7);

        // Parse JWT
        AuthInfo authInfo = JwtUtil.parseToken(token);

        // Load RBAC permissions for employee
        if (UserType.EMPLOYEE.equals(
                authInfo.getUserType()
        )) {

            PermissionContext.setPermissions(
                    permissionProvider.loadPermissions(
                            authInfo.getRole()
                    )
            );
        }

        // Current request URI
        String requestUri = request.getRequestURI();

        // Current login user type
        String userType = authInfo.getUserType();

        // EMPLOYEE can only access employee APIs
        if (UserType.EMPLOYEE.equals(userType)) {

            boolean allowed =
                    EMPLOYEE_APIS.stream()
                            .anyMatch(requestUri::startsWith);

            if (!allowed) {

                throw new BusinessException(
                        ErrorCode.NO_PERMISSION,
                        ErrorMessage.NO_PERMISSION
                );
            }
        }

        // USER can only access user APIs
        if (UserType.USER.equals(userType)) {

            boolean allowed =
                    USER_APIS.stream()
                            .anyMatch(requestUri::startsWith);

            if (!allowed) {

                throw new BusinessException(
                        ErrorCode.NO_PERMISSION,
                        ErrorMessage.NO_PERMISSION
                );
            }
        }

        // Store authentication info in ThreadLocal
        AuthContext.setCurrentUserId(authInfo.getUserId());

        AuthContext.setCurrentMerchantId(authInfo.getMerchantId());

        AuthContext.setCurrentUserType(authInfo.getUserType());

        // Load RBAC permissions for employee
        if (UserType.EMPLOYEE.equals(userType)) {

            PermissionContext.setPermissions(
                    permissionProvider.loadPermissions(
                            authInfo.getRole()
                    )
            );
        }
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

        PermissionContext.clear();
    }
}
