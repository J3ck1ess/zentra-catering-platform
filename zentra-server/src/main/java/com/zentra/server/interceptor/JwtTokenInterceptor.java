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
import com.zentra.server.service.JwtBlacklistService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;

/**
 * JWT authentication interceptor
 */
@Component
@Slf4j
@RequiredArgsConstructor
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
     * JWT blacklist service
     */
    private final JwtBlacklistService jwtBlacklistService;

    /**
     * Execute before controller
     */
    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) {

        // Current request URI
        String requestUri = request.getRequestURI();

        // Retrieve Authorization header
        String authHeader = request.getHeader("Authorization");

        // Validate header format
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {

            log.warn(
                    "[AUTH] Missing or invalid authorization header. uri={}",
                    requestUri
            );

            throw new BusinessException(
                    ErrorCode.TOKEN_INVALID,
                    ErrorMessage.TOKEN_INVALID
            );
        }

        // Extract token by removing "Bearer " prefix
        String token = authHeader.substring(7);

        log.info(
                "[AUTH] JWT validation started. uri={}",
                requestUri
        );

        // Parse JWT
        AuthInfo authInfo = JwtUtil.parseToken(token);

        // Check token blacklist
        if (jwtBlacklistService.isBlacklisted(token)) {

            log.warn(
                    "[AUTH] Blacklisted token detected. userId={}, uri={}",
                    authInfo.getUserId(),
                    requestUri
            );

            throw new BusinessException(
                    ErrorCode.TOKEN_BLACKLISTED,
                    ErrorMessage.TOKEN_BLACKLISTED
            );
        }

        // Current login user type
        String userType = authInfo.getUserType();

        // EMPLOYEE can only access employee APIs
        if (UserType.EMPLOYEE.equals(userType)) {

            boolean allowed =
                    EMPLOYEE_APIS.stream()
                            .anyMatch(requestUri::startsWith);

            if (!allowed) {

                log.warn(
                        "[AUTH] Employee access denied. userId={}, uri={}",
                        authInfo.getUserId(),
                        requestUri
                );

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

                log.warn(
                        "[AUTH] User access denied. userId={}, uri={}",
                        authInfo.getUserId(),
                        requestUri
                );

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

        AuthContext.setCurrentRole(authInfo.getRole());

        log.info(
                "[AUTH] Authentication successful. userId={}, merchantId={}, userType={}, uri={}",
                authInfo.getUserId(),
                authInfo.getMerchantId(),
                authInfo.getUserType(),
                requestUri
        );

        // Load RBAC permissions for employee
        if (UserType.EMPLOYEE.equals(userType)) {

            PermissionContext.setPermissions(

                    permissionProvider.loadPermissions(
                            authInfo.getRole()
                    )
            );

            log.info(
                    "[RBAC] Permissions loaded. role={}, permissionCount={}",
                    authInfo.getRole(),
                    PermissionContext.getPermissions().size()
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

        log.debug(
                "[AUTH] ThreadLocal context cleared. uri={}",
                request.getRequestURI()
        );
    }
}
