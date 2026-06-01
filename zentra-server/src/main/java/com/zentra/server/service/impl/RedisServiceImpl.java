package com.zentra.server.service.impl;

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

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(
            String key,
            Class<T> clazz
    ) {

        Object value =
                redisTemplate.opsForValue().get(key);

        if (value == null) {

            log.info(
                    "[CACHE] Cache miss. key={}",
                    key
            );

            return null;
        } else {

            log.info(
                    "[CACHE] Cache hit. key={}",
                    key
            );
        }

        return (T) value;
    }

    @Override
    public void delete(String key) {

        redisTemplate.delete(key);

        log.info(
                "[CACHE] Cache key deleted. key={}",
                key
        );
    }

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

        log.info(
                "[LOCK] Distributed lock acquisition attempted. key={}, success={}",
                key,
                Boolean.TRUE.equals(success)
        );
        return Boolean.TRUE.equals(success);
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
