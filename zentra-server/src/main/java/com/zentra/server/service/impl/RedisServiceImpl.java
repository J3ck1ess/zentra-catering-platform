package com.zentra.server.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zentra.server.service.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * Redis infrastructure service implementation
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RedisServiceImpl implements RedisService {

    /**
     * Redis template
     */
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Jackson object mapper
     */
    private final ObjectMapper objectMapper;

    /**
     * Get cache value and handle cache hit/miss logging
     */
    private Object getValue(
            String key
    ) {

        Object value =
                redisTemplate.opsForValue().get(key);

        if (value == null) {

            log.info(
                    "[CACHE] Cache miss. key={}",
                    key
            );

            return null;
        }

        log.info(
                "[CACHE] Cache hit. key={}",
                key
        );

        return value;
    }

    /**
     * Set redis value with TTL
     */
    @Override
    public void set(
            String key,
            Object value,
            Duration ttl
    ) {

        redisTemplate.opsForValue().set(
                key,
                value,
                ttl
        );
        log.info(
                "[CACHE] Cache value set. key={}, ttlSeconds={}",
                key,
                ttl.getSeconds()
        );
    }

    /**
     * Get redis value
     */
    @Override
    public <T> T get(
            String key,
            Class<T> clazz
    ) {

        Object value = getValue(key);

        if (value == null) {
            return null;
        }

        return objectMapper.convertValue(
                value,
                clazz
        );
    }


    /**
     * Get redis value with generic type support
     */
    @Override
    public <T> T get(
            String key,
            TypeReference<T> typeReference
    ) {

        Object value = getValue(key);

        if (value == null) {
            return null;
        }

        return objectMapper.convertValue(
                value,
                typeReference
        );
    }

    /**
     * Delete redis key
     */
    @Override
    public void delete(String key) {

        redisTemplate.delete(key);

        log.info(
                "[CACHE] Cache key deleted. key={}",
                key
        );
    }

    /**
     * Check redis key exists
     */
    @Override
    public boolean exists(String key) {

        Boolean exists = redisTemplate.hasKey(key);

        log.info(
                "[CACHE] Cache existence checked. key={}, exists={}",
                key,
                Boolean.TRUE.equals(exists)
        );

        return Boolean.TRUE.equals(exists);
    }

    /**
     * Set value if absent
     */
    @Override
    public boolean setIfAbsent(
            String key,
            Object value,
            Duration ttl
    ) {

        Boolean success =
                redisTemplate.opsForValue()
                        .setIfAbsent(
                                key,
                                value,
                                ttl
                        );

        boolean result = Boolean.TRUE.equals(success);

        log.info(
                "[CACHE] Cache set-if-absent executed. key={}, success={}",
                key,
                result
        );

        return result;
    }

    /**
     * Increment redis value atomically
     */
    @Override
    public Long increment(
            String key,
            Duration ttl
    ) {

        Long value =
                redisTemplate.opsForValue()
                        .increment(key);

        log.info(
                "[CACHE] Cache counter incremented. key={}, currentValue={}",
                key,
                value
        );

        if (value != null && value == 1) {

            redisTemplate.expire(
                    key,
                    ttl
            );

            log.info(
                    "[CACHE] Counter TTL initialized. key={}, ttlSeconds={}",
                    key,
                    ttl.getSeconds()
            );
        }

        return value;
    }

    /**
     * Try to acquire distributed lock
     */
    @Override
    public boolean tryLock(
            String key,
            String value,
            Duration ttl
    ) {

        Boolean success =
                redisTemplate.opsForValue().setIfAbsent(
                        key,
                        value,
                        ttl
                );

        boolean result = Boolean.TRUE.equals(success);

        log.info(
                "[LOCK] Distributed lock acquisition attempted. key={}, success={}",
                key,
                result
        );
        return result;
    }

    /**
     * Release distributed lock
     */
    @Override
    public void unlock(
            String key,
            String value
    ) {

        Object currentValue =
                redisTemplate.opsForValue().get(key);

        if (value.equals(currentValue)) {

            redisTemplate.delete(key);

            log.info(
                    "[LOCK] Distributed lock released. key={}",
                    key
            );
        } else {
            log.warn(
                    "[LOCK] Distributed lock release rejected. key={}",
                    key
            );
        }
    }
}
