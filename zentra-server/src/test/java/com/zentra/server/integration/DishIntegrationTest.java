package com.zentra.server.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zentra.common.constant.RedisKeyConstants;
import com.zentra.server.dto.DishCreateDTO;
import com.zentra.server.dto.DishUpdateDTO;
import com.zentra.server.dto.EmployeeLoginDTO;
import com.zentra.server.service.RedisService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class DishIntegrationTest extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RedisService redisService;

    // ==================== Create ====================
    @Test
    void createDish_shouldPersistDishSuccessfully() throws Exception {
        String dishName = "Integration Test Dish " + UUID.randomUUID();

        EmployeeLoginDTO loginRequest = new EmployeeLoginDTO();
        loginRequest.setUsername("admin");
        loginRequest.setPassword("123456");

        String loginResponse = mockMvc.perform(
                        post("/employee/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String jwt = objectMapper.readTree(loginResponse)
                .path("data")
                .path("token")
                .asText();

        Long dishId = null;

        try {
            DishCreateDTO createRequest = new DishCreateDTO();
            createRequest.setName(dishName);
            createRequest.setPrice(new BigDecimal("18.80"));
            createRequest.setCategoryId(2L);
            createRequest.setStatus(1);

            mockMvc.perform(
                            post("/dish")
                                    .header("Authorization", "Bearer " + jwt)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(createRequest))
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(1));

            String listResponse = mockMvc.perform(
                            get("/dish/list")
                                    .param("categoryId", "2")
                                    .header("Authorization", "Bearer " + jwt)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(1))
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            JsonNode dishes = objectMapper.readTree(listResponse)
                    .path("data");

            for (JsonNode dish : dishes) {
                if (dishName.equals(dish.path("name").asText())) {
                    dishId = dish.path("id").asLong();
                    break;
                }
            }

            assertThat(dishId).isNotNull();

            mockMvc.perform(
                            get("/dish/" + dishId)
                                    .header("Authorization", "Bearer " + jwt)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(1))
                    .andExpect(jsonPath("$.data.id").value(dishId))
                    .andExpect(jsonPath("$.data.name").value(dishName))
                    .andExpect(jsonPath("$.data.price").value(18.80))
                    .andExpect(jsonPath("$.data.status").value(1))
                    .andExpect(jsonPath("$.data.categoryId").value(2));

        } finally {
            if (dishId != null) {
                mockMvc.perform(
                        delete("/dish/" + dishId)
                                .header("Authorization", "Bearer " + jwt)
                );
            }
        }
    }

    // ==================== Get ====================
    @Test
    void getDishDetail_shouldCacheResultInRedis() throws Exception {
        String dishName = "Integration Cache Dish " + UUID.randomUUID();

        EmployeeLoginDTO loginRequest = new EmployeeLoginDTO();
        loginRequest.setUsername("admin");
        loginRequest.setPassword("123456");

        String loginResponse = mockMvc.perform(
                        post("/employee/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String jwt = objectMapper.readTree(loginResponse)
                .path("data")
                .path("token")
                .asText();

        Long dishId = null;

        try {
            DishCreateDTO createRequest = new DishCreateDTO();
            createRequest.setName(dishName);
            createRequest.setPrice(new BigDecimal("25.80"));
            createRequest.setCategoryId(2L);
            createRequest.setStatus(1);

            mockMvc.perform(
                            post("/dish")
                                    .header("Authorization", "Bearer " + jwt)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(createRequest))
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(1));

            String listResponse = mockMvc.perform(
                            get("/dish/list")
                                    .param("categoryId", "2")
                                    .header("Authorization", "Bearer " + jwt)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(1))
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            JsonNode dishes = objectMapper.readTree(listResponse)
                    .path("data");

            for (JsonNode dish : dishes) {
                if (dishName.equals(dish.path("name").asText())) {
                    dishId = dish.path("id").asLong();
                    break;
                }
            }

            assertThat(dishId).isNotNull();

            String cacheKey = RedisKeyConstants.DISH_DETAIL_CACHE + dishId;

            redisService.delete(cacheKey);

            assertThat(redisService.exists(cacheKey)).isFalse();

            mockMvc.perform(
                            get("/dish/" + dishId)
                                    .header("Authorization", "Bearer " + jwt)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(1))
                    .andExpect(jsonPath("$.data.id").value(dishId))
                    .andExpect(jsonPath("$.data.name").value(dishName))
                    .andExpect(jsonPath("$.data.price").value(25.80))
                    .andExpect(jsonPath("$.data.status").value(1))
                    .andExpect(jsonPath("$.data.categoryId").value(2));

            assertThat(redisService.exists(cacheKey)).isTrue();

            mockMvc.perform(
                            get("/dish/" + dishId)
                                    .header("Authorization", "Bearer " + jwt)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(1))
                    .andExpect(jsonPath("$.data.id").value(dishId))
                    .andExpect(jsonPath("$.data.name").value(dishName));

        } finally {
            if (dishId != null) {
                mockMvc.perform(
                        delete("/dish/" + dishId)
                                .header("Authorization", "Bearer " + jwt)
                );
            }
        }
    }

    // ==================== Update ====================
    @Test
    void updateDish_shouldPersistChangesAndEvictDetailCache() throws Exception {
        String uniqueSuffix = UUID.randomUUID().toString().substring(0, 8);
        String originalName = "UpdDish-" + uniqueSuffix;
        String updatedName = "UpdDish-" + uniqueSuffix + "-U";

        EmployeeLoginDTO loginRequest = new EmployeeLoginDTO();
        loginRequest.setUsername("admin");
        loginRequest.setPassword("123456");

        String loginResponse = mockMvc.perform(
                        post("/employee/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String jwt = objectMapper.readTree(loginResponse)
                .path("data")
                .path("token")
                .asText();

        Long dishId = null;

        try {
            DishCreateDTO createRequest = new DishCreateDTO();
            createRequest.setName(originalName);
            createRequest.setPrice(new BigDecimal("20.00"));
            createRequest.setCategoryId(2L);
            createRequest.setStatus(1);

            mockMvc.perform(
                            post("/dish")
                                    .header("Authorization", "Bearer " + jwt)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(createRequest))
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(1));

            String listResponse = mockMvc.perform(
                            get("/dish/list")
                                    .param("categoryId", "2")
                                    .header("Authorization", "Bearer " + jwt)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(1))
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            JsonNode dishes = objectMapper.readTree(listResponse)
                    .path("data");

            for (JsonNode dish : dishes) {
                if (originalName.equals(dish.path("name").asText())) {
                    dishId = dish.path("id").asLong();
                    break;
                }
            }

            assertThat(dishId).isNotNull();

            String cacheKey = RedisKeyConstants.DISH_DETAIL_CACHE + dishId;

            redisService.delete(cacheKey);

            mockMvc.perform(
                            get("/dish/" + dishId)
                                    .header("Authorization", "Bearer " + jwt)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(1))
                    .andExpect(jsonPath("$.data.id").value(dishId))
                    .andExpect(jsonPath("$.data.name").value(originalName))
                    .andExpect(jsonPath("$.data.price").value(20.00));

            assertThat(redisService.exists(cacheKey)).isTrue();

            DishUpdateDTO updateRequest = new DishUpdateDTO();
            updateRequest.setId(dishId);
            updateRequest.setName(updatedName);
            updateRequest.setPrice(new BigDecimal("28.80"));
            updateRequest.setCategoryId(2L);
            updateRequest.setStatus(1);

            mockMvc.perform(
                            patch("/dish")
                                    .header("Authorization", "Bearer " + jwt)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(updateRequest))
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(1));

            assertThat(redisService.exists(cacheKey)).isFalse();

            mockMvc.perform(
                            get("/dish/" + dishId)
                                    .header("Authorization", "Bearer " + jwt)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(1))
                    .andExpect(jsonPath("$.data.id").value(dishId))
                    .andExpect(jsonPath("$.data.name").value(updatedName))
                    .andExpect(jsonPath("$.data.price").value(28.80))
                    .andExpect(jsonPath("$.data.status").value(1))
                    .andExpect(jsonPath("$.data.categoryId").value(2));

        } finally {
            if (dishId != null) {
                mockMvc.perform(
                        delete("/dish/" + dishId)
                                .header("Authorization", "Bearer " + jwt)
                );
            }
        }
    }

    // ==================== Delete ====================
    @Test
    void deleteDish_shouldDeleteDishAndEvictDetailCache() throws Exception {
        String dishName = "DelDish-" + UUID.randomUUID().toString().substring(0, 8);

        EmployeeLoginDTO loginRequest = new EmployeeLoginDTO();
        loginRequest.setUsername("admin");
        loginRequest.setPassword("123456");

        String loginResponse = mockMvc.perform(
                        post("/employee/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String jwt = objectMapper.readTree(loginResponse)
                .path("data")
                .path("token")
                .asText();

        Long dishId = null;

        try {
            DishCreateDTO createRequest = new DishCreateDTO();
            createRequest.setName(dishName);
            createRequest.setPrice(new BigDecimal("22.80"));
            createRequest.setCategoryId(2L);
            createRequest.setStatus(1);

            mockMvc.perform(
                            post("/dish")
                                    .header("Authorization", "Bearer " + jwt)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(createRequest))
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(1));

            String listResponse = mockMvc.perform(
                            get("/dish/list")
                                    .param("categoryId", "2")
                                    .header("Authorization", "Bearer " + jwt)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(1))
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            JsonNode dishes = objectMapper.readTree(listResponse)
                    .path("data");

            for (JsonNode dish : dishes) {
                if (dishName.equals(dish.path("name").asText())) {
                    dishId = dish.path("id").asLong();
                    break;
                }
            }

            assertThat(dishId).isNotNull();

            String cacheKey = RedisKeyConstants.DISH_DETAIL_CACHE + dishId;

            mockMvc.perform(
                            get("/dish/" + dishId)
                                    .header("Authorization", "Bearer " + jwt)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(1))
                    .andExpect(jsonPath("$.data.id").value(dishId))
                    .andExpect(jsonPath("$.data.name").value(dishName))
                    .andExpect(jsonPath("$.data.price").value(22.80))
                    .andExpect(jsonPath("$.data.status").value(1))
                    .andExpect(jsonPath("$.data.categoryId").value(2));

            assertThat(redisService.exists(cacheKey)).isTrue();

            mockMvc.perform(
                            delete("/dish/" + dishId)
                                    .header("Authorization", "Bearer " + jwt)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(1));

            assertThat(redisService.exists(cacheKey)).isFalse();

            mockMvc.perform(
                            get("/dish/" + dishId)
                                    .header("Authorization", "Bearer " + jwt)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(51001))
                    .andExpect(jsonPath("$.msg").value("Dish not found"));

        } finally {
            if (dishId != null) {
                mockMvc.perform(
                        delete("/dish/" + dishId)
                                .header("Authorization", "Bearer " + jwt)
                );
            }
        }
    }

}