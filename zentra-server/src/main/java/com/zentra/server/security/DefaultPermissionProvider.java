package com.zentra.server.security;

import com.zentra.common.constant.PermissionConstants;
import com.zentra.common.constant.RoleConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

/**
 * Default RBAC permission provider
 */
@Component
@Slf4j
public class DefaultPermissionProvider implements PermissionProvider {

    /**
     * Immutable role-permission matrix
     */
    private static final Map<String, Set<String>> ROLE_PERMISSION_MATRIX = Map.of(

            // Super admin permissions
            RoleConstants.SUPER_ADMIN,
            Set.of(

                    PermissionConstants.EMPLOYEE_VIEW,
                    PermissionConstants.EMPLOYEE_CREATE,
                    PermissionConstants.EMPLOYEE_UPDATE,
                    PermissionConstants.EMPLOYEE_DELETE,

                    PermissionConstants.USER_VIEW,
                    PermissionConstants.USER_UPDATE,

                    PermissionConstants.CATEGORY_VIEW,
                    PermissionConstants.CATEGORY_CREATE,
                    PermissionConstants.CATEGORY_UPDATE,
                    PermissionConstants.CATEGORY_DELETE,

                    PermissionConstants.DISH_VIEW,
                    PermissionConstants.DISH_CREATE,
                    PermissionConstants.DISH_UPDATE,
                    PermissionConstants.DISH_DELETE,

                    PermissionConstants.ORDER_VIEW,
                    PermissionConstants.ORDER_UPDATE
            ),

            RoleConstants.STORE_MANAGER,
            Set.of(

                    PermissionConstants.EMPLOYEE_VIEW,

                    PermissionConstants.USER_VIEW,

                    PermissionConstants.CATEGORY_VIEW,
                    PermissionConstants.CATEGORY_CREATE,
                    PermissionConstants.CATEGORY_UPDATE,

                    PermissionConstants.DISH_VIEW,
                    PermissionConstants.DISH_CREATE,
                    PermissionConstants.DISH_UPDATE,

                    PermissionConstants.ORDER_VIEW,
                    PermissionConstants.ORDER_UPDATE
            ),

            RoleConstants.CASHIER,
            Set.of(

                    PermissionConstants.USER_VIEW,

                    PermissionConstants.ORDER_VIEW,
                    PermissionConstants.ORDER_UPDATE
            ),

            RoleConstants.KITCHEN_STAFF,
            Set.of(

                    PermissionConstants.ORDER_VIEW,
                    PermissionConstants.ORDER_UPDATE
            )
    );

    // TODO: need Review
    @Override
    public Set<String> loadPermissions(String role) {

        Set<String> permissions = ROLE_PERMISSION_MATRIX.getOrDefault(
                role,
                Set.of()
        );

        log.info(
                "[RBAC] Role permissions loaded. role={}, permissions={}",
                role,
                permissions.size()
        );

        return permissions;
    }
}
