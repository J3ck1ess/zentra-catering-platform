package com.zentra.server.interceptor;

import com.zentra.common.constant.ErrorCode;
import com.zentra.common.constant.ErrorMessage;
import com.zentra.common.context.PermissionContext;
import com.zentra.common.exception.BusinessException;
import com.zentra.server.annotation.RequirePermission;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Set;

/**
 * RBAC permission interceptor
 */
@Component
@Slf4j
public class PermissionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) {

        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        RequirePermission requirePermission =
                handlerMethod.getMethodAnnotation(
                        RequirePermission.class
                );

        if (requirePermission == null) {
            return true;
        }

        String requiredPermission = requirePermission.value();

        log.info(
                "[RBAC] Permission validation started. permission={}, uri={}",
                requiredPermission,
                request.getRequestURI()
        );

        Set<String> permissions =
                PermissionContext.getPermissions();

        if (permissions == null
                || !permissions.contains(requiredPermission)) {

            log.warn(
                    "[RBAC] Permission denied. permission={}, uri={}",
                    requiredPermission,
                    request.getRequestURI()
            );

            throw new BusinessException(
                    ErrorCode.NO_PERMISSION,
                    ErrorMessage.NO_PERMISSION
            );
        }

        log.info(
                "[RBAC] Permission granted. permission={}, uri={}",
                requiredPermission,
                request.getRequestURI()
        );

        return true;
    }
}
