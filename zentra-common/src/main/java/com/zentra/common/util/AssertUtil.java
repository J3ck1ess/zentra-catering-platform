package com.zentra.common.util;

public class AssertUtil {

    /**
     * Check DB affected rows
     */
    public static void checkRows(int rows, String message) {
        if (rows == 0) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Check object not null
     */
    public static void notNull(Object obj, String message) {
        if (obj == null) {
            throw new IllegalArgumentException(message);
        }
    }
}
