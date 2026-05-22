package com.zentra.server.service;

/**
 * Verification code service
 */
public interface VerificationCodeService {

    /**
     * Generate verification code
     */
    String generateCode();

    /**
     * Save verification code
     */
    void saveCode(
            String type,
            String target,
            String code
    );

    /**
     * Validate verification code
     */
    boolean validateCode(
            String type,
            String target,
            String code
    );

    /**
     * Delete verification code
     */
    void deleteCode(
            String type,
            String target
    );

    /**
     * Check verification retry limit
     */
    boolean isRetryAllowed(
            String type,
            String target
    );

    /**
     * Increment verification retry count
     */
    void incrementRetryCount(
            String type,
            String target
    );
}
