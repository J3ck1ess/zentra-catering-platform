package com.zentra.common.constant;

/**
 * Redis key namespace constants
 */
public final class RedisKeyConstants {

    /**
     * JWT token blacklist key
     */
    public static final String JWT_TOKEN_BLACKLIST =
            "auth:blacklist:jwt:";

    /**
     * Login verification code key
     */
    public static final String LOGIN_VERIFICATION_CODE =
            "auth:verification:login:";

    /**
     * Verification retry count key
     */
    public static final String VERIFICATION_RETRY_COUNT =
            "auth:verification:retry:";

    /**
     * Login rate limit key
     */
    public static final String LOGIN_RATE_LIMIT =
            "auth:login:limit:";

    /**
     * User profile cache key
     */
    public static final String USER_PROFILE_CACHE =
            "cache:user:profile:";

    /**
     * Distributed lock key
     */
    public static final String DISTRIBUTED_LOCK =
            "lock:";

    private RedisKeyConstants() {
        // prevent instantiation
    }
}
