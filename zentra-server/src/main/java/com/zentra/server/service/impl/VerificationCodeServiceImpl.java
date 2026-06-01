package com.zentra.server.service.impl;

import com.zentra.common.constant.RedisKeyConstants;
import com.zentra.common.constant.RedisTtlConstants;
import com.zentra.server.service.RedisService;
import com.zentra.server.service.VerificationCodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * Verification code service implementation
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class VerificationCodeServiceImpl implements VerificationCodeService {

    /**
     * Redis service
     */
    private final RedisService redisService;

    /**
     * Random generator
     */
    private final Random random = new Random();

    /**
     * Maximum verification retry count
     */
    private static final int MAX_RETRY_COUNT = 5;

    @Override
    public String generateCode() {

        int code = 100000 + random.nextInt(900000);

        return String.valueOf(code);
    }

    @Override
    public void saveCode(
            String type,
            String target,
            String code
    ) {

        String key = buildKey(type, target);

        redisService.set(
                key,
                code,
                RedisTtlConstants.LOGIN_VERIFICATION_CODE_TTL
        );

        log.info(
                "[AUTH] Verification code saved. type={}, target={}",
                type,
                target
        );
    }

    @Override
    public boolean validateCode(
            String type,
            String target,
            String code
    ) {

        String key = buildKey(type, target);

        String storedCode = redisService.get(
                key,
                String.class
        );

        boolean valid = storedCode != null && code.equals(storedCode);

        if (valid) {
            log.info(
                    "[AUTH] Verification code validated successfully. type={}, target={}",
                    type,
                    target
            );
        } else {
            log.warn(
                    "[AUTH] Verification code validation failed. type={}, target={}",
                    type,
                    target
            );
        }
        return valid;
    }

    @Override
    public void deleteCode(
            String type,
            String target
    ) {

        String key = buildKey(type, target);

        redisService.delete(key);
        log.info(
                "[AUTH] Verification code deleted. type={}, target={}",
                type,
                target
        );
    }

    @Override
    public boolean isRetryAllowed(
            String type,
            String target
    ) {

        String retryKey = buildRetryKey(type, target);

        Integer retryCount =
                redisService.get(
                        retryKey,
                        Integer.class
                );

        if (retryCount == null) {
            log.info(
                    "[AUTH] Verification retry check passed. type={}, target={}, retryCount=0",
                    type,
                    target
            );
            return true;
        }

        boolean allowed = retryCount < MAX_RETRY_COUNT;

        if (!allowed) {
            log.warn(
                    "[AUTH] Verification retry limit exceeded. type={}, target={}, retryCount={}",
                    type,
                    target,
                    retryCount
            );
        }

        return allowed;
    }

    @Override
    public void incrementRetryCount(
            String type,
            String target
    ) {

        String retryKey = buildRetryKey(type, target);

        Integer retryCount =
                redisService.get(
                        retryKey,
                        Integer.class
                );

        if (retryCount == null) {

            redisService.set(
                    retryKey,
                    1,
                    RedisTtlConstants.VERIFICATION_RETRY_TTL
            );

            log.warn(
                    "[AUTH] Verification retry count initialized. type={}, target={}, retryCount=1",
                    type,
                    target
            );

            return;
        }

        redisService.set(
                retryKey,
                retryCount + 1,
                RedisTtlConstants.VERIFICATION_RETRY_TTL
        );

        log.warn(
                "[AUTH] Verification retry count incremented. type={}, target={}, retryCount={}",
                type,
                target,
                retryCount + 1
        );
    }

    /**
     * Build verification code redis key
     */
    private String buildKey(
            String type,
            String target
    ) {

        return RedisKeyConstants
                .LOGIN_VERIFICATION_CODE
                + type
                + ":"
                + target;
    }

    /**
     * Build verification retry redis key
     */
    private String buildRetryKey(
            String type,
            String target
    ) {

        return RedisKeyConstants
                .VERIFICATION_RETRY_COUNT
                + type
                + ":"
                + target;
    }
}

