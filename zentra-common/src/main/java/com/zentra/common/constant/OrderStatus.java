package com.zentra.common.constant;

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

    private OrderStatus() {
        // prevent instantiation
    }
}