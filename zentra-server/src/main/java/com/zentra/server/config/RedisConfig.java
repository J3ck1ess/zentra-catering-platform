package com.zentra.server.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis infrastructure configuration
 */
@Configuration
public class RedisConfig {

    /**
     * Redis template configuration
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(
            RedisConnectionFactory connectionFactory,
            ObjectMapper objectMapper
    ) {

        RedisTemplate<String, Object> redisTemplate =
                new RedisTemplate<>();

        // Set redis connection factory
        redisTemplate.setConnectionFactory(
                connectionFactory

        );

        // JSON serializer
        Jackson2JsonRedisSerializer<Object> jsonSerializer =
                new Jackson2JsonRedisSerializer<>(
                        objectMapper,
                        Object.class
                );

        // String key serializer
        StringRedisSerializer stringSerializer =
                new StringRedisSerializer();

        // Key serializer
        redisTemplate.setKeySerializer(
                stringSerializer
        );

        // Value serializer
        redisTemplate.setValueSerializer(
                jsonSerializer
        );

        // Hash key serializer
        redisTemplate.setHashKeySerializer(
                stringSerializer
        );

        // Hash value serializer
        redisTemplate.setHashValueSerializer(
                jsonSerializer
        );

        // Initialize redis template
        redisTemplate.afterPropertiesSet();

        return redisTemplate;
    }
}
