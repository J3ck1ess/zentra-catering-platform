package com.zentra.common.context;

import java.util.Set;

/**
 * Permission context holder
 */
public final class PermissionContext {

    private static final ThreadLocal<Set<String>> CURRENT_PERMISSIONS =
            new ThreadLocal<>();

    /**
     * Set current permissions
     */
    public static void setPermissions(Set<String> permissions) {

        CURRENT_PERMISSIONS.set(permissions);
    }

    /**
     * Get current permissions
     */
    public static Set<String> getPermissions() {

        return CURRENT_PERMISSIONS.get();
    }

    /**
     * Clear current permissions
     */
    public static void clear() {

        CURRENT_PERMISSIONS.remove();
    }

    private PermissionContext() {
        // prevent instantiation
    }
}
