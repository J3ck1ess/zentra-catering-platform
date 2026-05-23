package com.zentra.server.service.impl;

import com.zentra.common.constant.RedisKeyConstants;
import com.zentra.server.service.JwtBlacklistService;
import com.zentra.server.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * JWT blacklist service implementation
 */
@Service
@RequiredArgsConstructor
public class JwtBlacklistServiceImpl implements JwtBlacklistService {

    /**
     * Redis service
     */
    private final RedisService redisService;

    /**
     * Add token to blacklist
     */
    @Override
    public void blacklistToken(
            String token,
            Duration ttl
    ) {

        redisService.set(
                buildBlacklistKey(token),
                true,
                ttl
        );
    }

    /**
     * Check whether token is blacklisted
     */
    @Override
    public boolean isBlacklisted(String token) {

        return redisService.exists(
                buildBlacklistKey(token)
        );
    }

    /**
     * Build JWT blacklist redis key
     */
    private String buildBlacklistKey(String token) {

        return RedisKeyConstants
                .JWT_TOKEN_BLACKLIST
                + token;
    }
}
