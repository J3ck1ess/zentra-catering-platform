package com.zentra.common.constant;

import java.time.Duration;

/**
 * Redis TTL constants
 */
public final class RedisTtlConstants {

    /**
     * Login verification code TTL
     */
    public static final Duration
            LOGIN_VERIFICATION_CODE_TTL =
            Duration.ofMinutes(5);

    /**
     * Verification retry TTL
     */
    public static final Duration
            VERIFICATION_RETRY_TTL =
            Duration.ofMinutes(5);

    /**
     * Login rate limit TTL
     */
    public static final Duration
            LOGIN_RATE_LIMIT_TTL =
            Duration.ofMinutes(1);

    /**
     * User profile cache TTL
     */
    public static final Duration
            USER_PROFILE_CACHE_TTL =
            Duration.ofMinutes(30);

    /**
     * Dish detail cache TTL
     */
    public static final Duration
            DISH_DETAIL_CACHE_TTL =
            Duration.ofMinutes(30);

    /**
     * Distributed lock TTL
     */
    public static final Duration
            DISTRIBUTED_LOCK_TTL =
            Duration.ofSeconds(10);

    private RedisTtlConstants() {
        // prevent instantiation
    }
}
