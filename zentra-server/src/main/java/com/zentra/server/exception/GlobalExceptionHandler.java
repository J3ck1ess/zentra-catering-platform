package com.zentra.server.exception;

import com.zentra.common.result.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler for handling all runtime exceptions
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle runtime exceptions
     *
     * @param e exception
     * @return standardized error response
     */
    @ExceptionHandler(RuntimeException.class)
    public Result<String> handleRuntimeException(RuntimeException e) {

        return Result.error(401, e.getMessage());
    }

    /**
     * Handle generic exceptions
     *
     * @param e exception
     * @return standardized error response
     */
    @ExceptionHandler(Exception.class)
    public Result<String> handleException(Exception e) {

        return Result.error(500, "Internal server error");
    }
}
