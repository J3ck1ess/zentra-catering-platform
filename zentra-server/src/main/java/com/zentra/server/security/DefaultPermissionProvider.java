package com.zentra.server.security;

import com.zentra.common.constant.PermissionConstants;
import com.zentra.common.constant.RoleConstants;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Default RBAC permission provider
 */
@Component
public class DefaultPermissionProvider implements PermissionProvider {

    @Override
    public Set<String> loadPermissions(String role) {

        // Super admin permissions
        if (RoleConstants.SUPER_ADMIN.equals(role)) {

            return Set.of(

                    // Employee permissions
                    PermissionConstants.EMPLOYEE_VIEW,
                    PermissionConstants.EMPLOYEE_CREATE,
                    PermissionConstants.EMPLOYEE_UPDATE,
                    PermissionConstants.EMPLOYEE_DELETE,

                    // User permissions
                    PermissionConstants.USER_VIEW,
                    PermissionConstants.USER_UPDATE,

                    // Category permissions
                    PermissionConstants.CATEGORY_VIEW,
                    PermissionConstants.CATEGORY_CREATE,
                    PermissionConstants.CATEGORY_UPDATE,
                    PermissionConstants.CATEGORY_DELETE,

                    // Dish permissions
                    PermissionConstants.DISH_VIEW,
                    PermissionConstants.DISH_CREATE,
                    PermissionConstants.DISH_UPDATE,
                    PermissionConstants.DISH_DELETE,

                    // Order permissions
                    PermissionConstants.ORDER_VIEW,
                    PermissionConstants.ORDER_UPDATE
            );
        }

        // Default empty permissions
        return Set.of();
    }
}
