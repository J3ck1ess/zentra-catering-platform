package com.zentra.common.constant;

import java.util.Set;

/**
 * User status constants
 */
public class UserStatus {

    /**
     * Active user
     */
    public static final int ACTIVE = 1;

    /**
     * Disabled user
     */
    public static final int DISABLED = 0;

    /**
     * Valid status set
     */
    private static final Set<Integer> VALID_STATUS = Set.of(

            ACTIVE,
            DISABLED
    );

    /**
     * Check whether status is valid
     */
    public static boolean isValid(Integer status) {

        return VALID_STATUS.contains(status);
    }

    private UserStatus() {
    }
}
