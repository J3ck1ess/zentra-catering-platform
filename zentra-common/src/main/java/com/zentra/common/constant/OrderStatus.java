package com.zentra.common.constant;

import java.util.Set;

/**
 * Order status constants
 */
public class OrderStatus {

    /**
     * Pending (not paid)
     */
    public static final int PENDING = 1;

    /**
     * Paid
     */
    public static final int PAID = 2;

    /**
     * Completed
     */
    public static final int COMPLETED = 3;

    /**
     * Cancelled
     */
    public static final int CANCELLED = 4;

    /**
     * Valid status set
     */

    private static final Set<Integer> VALID_STATUS = Set.of(

            PENDING,
            PAID,
            COMPLETED,
            CANCELLED
    );

    /**
     * Check whether status is valid
     */
    public static boolean isValid(Integer status) {

        return VALID_STATUS.contains(status);
    }

    private OrderStatus() {
        // prevent instantiation
    }
}