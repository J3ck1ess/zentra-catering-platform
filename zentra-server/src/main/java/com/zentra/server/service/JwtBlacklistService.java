package com.zentra.server.service;

import java.time.Duration;

/**
 * JWT blacklist service
 */
public interface JwtBlacklistService {

    /**
     * Add token to blacklist
     */
    void blacklistToken(
            String token,
            Duration ttl
    );

    /**
     * Check whether token is blacklisted
     */
    boolean isBlacklisted(
            String token
    );
}
