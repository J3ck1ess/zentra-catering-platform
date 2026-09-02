package com.zentra.server.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zentra.common.auth.AuthInfo;
import com.zentra.common.constant.RedisKeyConstants;
import com.zentra.common.constant.UserType;
import com.zentra.common.util.JwtUtil;
import com.zentra.server.dto.UserRegisterDTO;
import com.zentra.server.dto.UserUpdateDTO;
import com.zentra.server.entity.User;
import com.zentra.server.mapper.UserMapper;
import com.zentra.server.service.RedisService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserIntegrationTest extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisService redisService;

    // ==================== Register ====================
    @Test
    void registerUser_shouldPersistUserSuccessfully() throws Exception {
        String username = "intuser" + UUID.randomUUID().toString().substring(0, 8);

        UserRegisterDTO registerRequest = new UserRegisterDTO();
        registerRequest.setUsername(username);
        registerRequest.setPassword("123456");
        registerRequest.setNickname("Integration User");
        registerRequest.setPhone("+77001234567");

        try {
            mockMvc.perform(
                            post("/user/register")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(registerRequest))
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(1));

            com.zentra.server.entity.User savedUser =
                    userMapper.findByUsername(username, 1L);

            assertThat(savedUser).isNotNull();
            assertThat(savedUser.getUsername()).isEqualTo(username);
            assertThat(savedUser.getNickname()).isEqualTo("Integration User");
            assertThat(savedUser.getPhone()).isEqualTo("+77001234567");
            assertThat(savedUser.getMerchantId()).isEqualTo(1L);
            assertThat(savedUser.getStatus()).isEqualTo(1);
            assertThat(savedUser.getPassword()).isNotEqualTo("123456");
        } finally {
            com.zentra.server.entity.User savedUser =
                    userMapper.findByUsername(username, 1L);

            if (savedUser != null) {
                // No delete operation is exposed by UserMapper.
                // The test database is reset before the integration test suite.
            }
        }
    }

    // ==================== Get ====================
    @Test
    void getUserProfile_shouldCacheResultInRedis() throws Exception {
        Long userId = 2L;
        Long merchantId = 1L;

        AuthInfo authInfo = new AuthInfo(
                userId,
                merchantId,
                UserType.USER,
                null
        );

        String jwt = JwtUtil.generateToken(authInfo);

        String cacheKey =
                RedisKeyConstants.USER_PROFILE_CACHE
                        + merchantId
                        + ":"
                        + userId;

        redisService.delete(cacheKey);

        assertThat(redisService.exists(cacheKey)).isFalse();

        mockMvc.perform(
                        get("/user/profile")
                                .header("Authorization", "Bearer " + jwt)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.data.id").value(userId));

        assertThat(redisService.exists(cacheKey)).isTrue();

        mockMvc.perform(
                        get("/user/profile")
                                .header("Authorization", "Bearer " + jwt)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.data.id").value(userId));
    }

    @Test
    void getUserDetail_shouldPreventCachePenetrationForNonexistentUser() throws Exception {
        Long nonexistentUserId = 999999L;
        Long merchantId = 1L;

        AuthInfo authInfo = new AuthInfo(
                2L,
                merchantId,
                UserType.USER,
                null
        );

        String jwt = JwtUtil.generateToken(authInfo);

        String cacheKey =
                RedisKeyConstants.USER_PROFILE_CACHE
                        + "public:"
                        + nonexistentUserId;

        redisService.delete(cacheKey);

        assertThat(redisService.exists(cacheKey)).isFalse();

        mockMvc.perform(
                        get("/user/" + nonexistentUserId)
                                .header("Authorization", "Bearer " + jwt)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(40201))
                .andExpect(jsonPath("$.msg").value("User not found"));

        assertThat(redisService.exists(cacheKey)).isTrue();

        mockMvc.perform(
                        get("/user/" + nonexistentUserId)
                                .header("Authorization", "Bearer " + jwt)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(40201))
                .andExpect(jsonPath("$.msg").value("User not found"));

        assertThat(redisService.exists(cacheKey)).isTrue();

        redisService.delete(cacheKey);
    }

    @Test
    void getUserDetail_shouldCacheExistingUser() throws Exception {
        Long userId = 2L;

        AuthInfo authInfo = new AuthInfo(
                userId,
                1L,
                UserType.USER,
                null
        );

        String jwt = JwtUtil.generateToken(authInfo);

        String cacheKey =
                RedisKeyConstants.USER_PROFILE_CACHE
                        + "public:"
                        + userId;

        redisService.delete(cacheKey);

        assertThat(redisService.exists(cacheKey)).isFalse();

        mockMvc.perform(
                        get("/user/" + userId)
                                .header("Authorization", "Bearer " + jwt)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.data.id").value(userId));

        assertThat(redisService.exists(cacheKey)).isTrue();

        mockMvc.perform(
                        get("/user/" + userId)
                                .header("Authorization", "Bearer " + jwt)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.data.id").value(userId));

        assertThat(redisService.exists(cacheKey)).isTrue();

        redisService.delete(cacheKey);
    }

    // ==================== Update ====================
    @Test
    void updateUserProfile_shouldPersistChangesAndEvictCache() throws Exception {
        Long userId = 2L;
        Long merchantId = 1L;

        AuthInfo authInfo = new AuthInfo(
                userId,
                merchantId,
                UserType.USER,
                null
        );

        String jwt = JwtUtil.generateToken(authInfo);

        String cacheKey =
                RedisKeyConstants.USER_PROFILE_CACHE
                        + merchantId
                        + ":"
                        + userId;

        String originalNickname = "Integration User";
        String updatedNickname = "Updated User";

        redisService.delete(cacheKey);

        mockMvc.perform(
                        get("/user/profile")
                                .header("Authorization", "Bearer " + jwt)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.data.id").value(userId));

        assertThat(redisService.exists(cacheKey)).isTrue();

        UserUpdateDTO updateRequest = new UserUpdateDTO();
        updateRequest.setNickname(updatedNickname);
        updateRequest.setPhone("+77001112233");

        mockMvc.perform(
                        put("/user/profile")
                                .header("Authorization", "Bearer " + jwt)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateRequest))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1));

        assertThat(redisService.exists(cacheKey)).isFalse();

        User updatedUser = userMapper.findById(userId, merchantId);

        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getNickname()).isEqualTo(updatedNickname);
        assertThat(updatedUser.getPhone()).isEqualTo("+77001112233");
    }

}