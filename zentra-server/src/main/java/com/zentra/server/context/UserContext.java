package com.zentra.server.context;

/**
 * Utility class for storing and retrieving current user information
 * using ThreadLocal (per-request storage)
 */
public class UserContext {

    // ThreadLocal to store user ID for each request thread
    private static final ThreadLocal<Long> currentUser = new ThreadLocal<>();

    /**
     * Set current user ID into ThreadLocal
     *
     * @param userId current user ID
     */
    public static void setCurrentUser(Long userId) {
        currentUser.set(userId);
    }

    /**
     * Get current user ID from ThreadLocal
     *
     * @return current user ID
     */
    public static Long getCurrentUser() {
        return currentUser.get();
    }

    /**
     * Clear ThreadLocal to prevent memory leak
     */
    public static void clear() {
        currentUser.remove();
    }
}
