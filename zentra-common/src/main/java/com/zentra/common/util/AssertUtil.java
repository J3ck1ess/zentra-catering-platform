package com.zentra.common.util;

import com.zentra.common.exception.BusinessException;

public class AssertUtil {

    /**
     * Assert affected rows > 0
     */
    public static void checkRows(
            int rows,
            Integer code,
            String message
    ) {
        if (rows <= 0) {

            throw new BusinessException(code, message);
        }
    }

    /**
     * Assert object is not null
     */
    public static void notNull(
            Object obj,
            Integer code,
            String message
    ) {
        if (obj == null) {

            throw new BusinessException(code, message);
        }
    }

    /**
     * Assert object is null
     */
    public static void isNull(
            Object obj,
            Integer code,
            String message
    ) {
        if (obj != null) {

            throw new BusinessException(code, message);
        }
    }

    /**
     * Assert condition is true
     */
    public static void isTrue(
            boolean condition,
            Integer code,
            String message
    ) {
        if (!condition){

            throw new BusinessException(code, message);
        }
    }
}
