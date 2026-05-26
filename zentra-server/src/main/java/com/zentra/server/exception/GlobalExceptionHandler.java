package com.zentra.server.exception;

import com.zentra.common.constant.ErrorCode;
import com.zentra.common.exception.BusinessException;
import com.zentra.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler for unified error response
 */
@Slf4j
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

        return Result.error(ErrorCode.FAILURE, errorMsg);
    }

    /**
     * Handle business exception
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException ex) {

        log.warn(
                "Business exception occurred. " +
                "code={}, message={}",
                ex.getCode(),
                ex.getMessage()
        );
        return Result.error(ex.getCode(), ex.getMessage());
    }

    /**
     * Handle all other exceptions
     */
    @ExceptionHandler(Exception.class)
    public Result<String> handleException(Exception e) {

        log.error(
                "Unhandled system exception occurred",
                e
        );

        // Fallback error (avoid exposing internal details)
        return Result.error(ErrorCode.FAILURE, "Internal server error");
    }

}