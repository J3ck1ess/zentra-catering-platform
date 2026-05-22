package com.zentra.common.constant;

/**
 * Redis key namespace constants
 */
public final class RedisKeyConstants {

    /**
     * JWT token blacklist key
     */
    public static final String TOKEN_BLACKLIST =
            "auth:blacklist:";

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

    private RedisKeyConstants() {
        // prevent instantiation
    }
}
