package com.zentra.server.service;

import com.fasterxml.jackson.core.type.TypeReference;

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
     * Get redis value with generic type support
     */
    <T> T get(
            String key,
            TypeReference<T> typeReference

    );

    /**
     * Delete redis key
     */
    void delete(String key);

    /**
     * Check redis key exists
     */
    boolean exists(String key);

    /**
     * Increment redis value atomically
     */
    Long increment(
            String key,
            Duration ttl
    );

    /**
     * Try to acquire distributed lock
     */
    boolean tryLock(
            String key,
            String value,
            Duration ttl
    );

    /**
     * Release distributed lock
     */
    void unlock(
            String key,
            String value
    );
}
