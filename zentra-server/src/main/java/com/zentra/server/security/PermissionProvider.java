package com.zentra.server.security;

import java.util.Set;

/**
 * Permission provider interface
 */
public interface PermissionProvider {

    /**
     * Load permissions by employee role
     */
    Set<String> loadPermissions(String role);
}
