package com.zentra.server.service.impl;

import com.zentra.server.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * Redis infrastructure service implementation
 */
@Service
@RequiredArgsConstructor
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
            return null;
        }

        return (T) value;
    }

    @Override
    public void delete(String key) {

        redisTemplate.delete(key);
    }

    @Override
    public boolean exists(String key) {

        Boolean exists = redisTemplate.hasKey(key);

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

        if (value != null && value == 1) {

            redisTemplate.expire(
                    key,
                    ttl
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
        }
    }
}
