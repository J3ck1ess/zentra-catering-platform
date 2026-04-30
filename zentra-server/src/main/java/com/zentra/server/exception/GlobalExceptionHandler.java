package com.zentra.server.exception;

import com.zentra.common.result.Result;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler for unified error response
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle validation exceptions (@Valid)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<String> handleValidationException(MethodArgumentNotValidException e) {

        // Extract first field error message
        String errorMsg = e.getBindingResult()
                .getFieldErrors()
                .get(0)
                .getDefaultMessage();

        return Result.error(400, errorMsg);
    }

    /**
     * Handle IllegalArgumentException
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public Result<String> handleIllegalArgumentException(IllegalArgumentException e) {

        return Result.error(400, e.getMessage());
    }

    /**
     * Handle all other exceptions
     */
    @ExceptionHandler(Exception.class)
    public Result<String> handleException(Exception e) {

        e.printStackTrace();

        // Fallback error (avoid exposing internal details)
        return Result.error(500, "Internal server error");
    }
}