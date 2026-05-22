package com.zentra.server.interceptor;

import com.zentra.common.constant.ErrorCode;
import com.zentra.common.constant.ErrorMessage;
import com.zentra.common.context.PermissionContext;
import com.zentra.common.exception.BusinessException;
import com.zentra.server.annotation.RequirePermission;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Set;

/**
 * RBAC permission interceptor
 */
@Component
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

        Set<String> permissions =
                PermissionContext.getPermissions();

        if (permissions == null
                || !permissions.contains(requiredPermission)) {

            throw new BusinessException(
                    ErrorCode.NO_PERMISSION,
                    ErrorMessage.NO_PERMISSION
            );
        }

        return true;
    }
}
