package com.zentra.server.service;

import java.time.Duration;

/**
 * Redis infrastructure service
 */
public interface RedisService {

    /**
     * Set redis value with TTL
     */
    void set(
            String key,
            Object value,
            Duration ttl
    );

    /**
     * Get redis value
     */
    <T> T get(
            String key,
            Class<T> clazz
    );

    /**
     * Delete redis key
     */
    void delete(String key);

    /**
     * Check redis key exists
     */
    boolean exists(String key);
}
