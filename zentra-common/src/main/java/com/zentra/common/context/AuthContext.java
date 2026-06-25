package com.zentra.common.context;

/**
 * ThreadLocal-based authentication context
 */
public class AuthContext {

    private static final ThreadLocal<Long> CURRENT_USER_ID = new ThreadLocal<>();

    private static final ThreadLocal<Long> CURRENT_MERCHANT_ID = new ThreadLocal<>();

    private static final ThreadLocal<String> CURRENT_USER_TYPE = new ThreadLocal<>();

    private static final ThreadLocal<String> CURRENT_ROLE = new ThreadLocal<>();

    /**
     * Set current user id
     */
    public static void setCurrentUserId(Long userId) {

        CURRENT_USER_ID.set(userId);
    }

    /**
     * Get current user id
     */
    public static Long getCurrentUserId() {

        return CURRENT_USER_ID.get();
    }

    /**
     * Set current merchant id
     */
    public static void setCurrentMerchantId(Long merchantId) {

        CURRENT_MERCHANT_ID.set(merchantId);
    }

    /**
     * Get current merchant id
     */
    public static Long getCurrentMerchantId() {

        return CURRENT_MERCHANT_ID.get();
    }

    /**
     * Set current user type
     */
    public static void setCurrentUserType(String userType) {

        CURRENT_USER_TYPE.set(userType);
    }

    /**
     * Get current user type
     */
    public static String getCurrentUserType() {

        return CURRENT_USER_TYPE.get();
    }

    /**
     * Set current role
     */
    public static void setCurrentRole(String role) {

        CURRENT_ROLE.set(role);
    }

    /**
     * Get current role
     */
    public static String getCurrentRole() {

        return CURRENT_ROLE.get();
    }

    /**
     * Clear ThreadLocal
     */
    public static void clear() {

        CURRENT_USER_ID.remove();
        CURRENT_MERCHANT_ID.remove();
        CURRENT_USER_TYPE.remove();
        CURRENT_ROLE.remove();
    }

    private AuthContext() {
    }

}
