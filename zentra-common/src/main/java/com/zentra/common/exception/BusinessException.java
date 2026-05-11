package com.zentra.common.exception;

/**
 * Custom business exception
 */
public class BusinessException extends RuntimeException{

    /**
     * Business error code
     */
    private final Integer code;

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
