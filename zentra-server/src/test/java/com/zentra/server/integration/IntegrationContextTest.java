package com.zentra.server.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;

class IntegrationContextTest extends IntegrationTestBase {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    void shouldConnectToTestDatabase() {

        Integer result = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM merchant",
                Integer.class
        );

        assertThat(result)
                .isEqualTo(1);
    }

    @Test
    void shouldConnectToTestRedis() {

        String key = "integration-test:connection";

        stringRedisTemplate.opsForValue()
                .set(key, "ok");

        String value = stringRedisTemplate.opsForValue()
                .get(key);

        assertThat(value)
                .isEqualTo("ok");

        stringRedisTemplate.delete(key);
    }
}