package com.zentra.common.constant;

import java.util.Set;

/**
 * Role constants for RBAC authorization
 */
public final class RoleConstants {

    /**
     * Super administrator role
     */
    public static final String SUPER_ADMIN = "SUPER_ADMIN";

    /**
     * Store manager role
     */
    public static final String STORE_MANAGER = "STORE_MANAGER";

    /**
     * Cashier role
     */
    public static final String CASHIER = "CASHIER";

    /**
     * Kitchen staff role
     */
    public static final String KITCHEN_STAFF = "KITCHEN_STAFF";

    /**
     * Valid role set
     */
    private static final Set<String> VALID_ROLES =

            Set.of(
                    SUPER_ADMIN,
                    STORE_MANAGER,
                    CASHIER,
                    KITCHEN_STAFF
            );

    /**
     * Validate employee role
     */
    public static boolean isValid(String role) {

        return VALID_ROLES.contains(role);
    }

    private RoleConstants() {
        // prevent instantiation
    }
}
