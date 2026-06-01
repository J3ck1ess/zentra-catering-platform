package com.zentra.server.service.impl;

import com.zentra.common.constant.RedisKeyConstants;
import com.zentra.server.service.JwtBlacklistService;
import com.zentra.server.service.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * JWT blacklist service implementation
 */
@Service
@RequiredArgsConstructor
@Slf4j
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

        log.info(
                "[AUTH] JWT token blacklisted. ttlSeconds={}",
                ttl.getSeconds()
        );
    }



    /**
     * Check whether token is blacklisted
     */
    @Override
    public boolean isBlacklisted(String token) {

        boolean blacklisted = redisService.exists(
                buildBlacklistKey(token)
        );

        if (blacklisted) {
            log.warn(
                    "[AUTH] Blacklisted token detected."
            );
        }

        return blacklisted;
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
