package com.zentra.server.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zentra.common.constant.ErrorCode;
import com.zentra.common.constant.RedisKeyConstants;
import com.zentra.server.dto.CategoryCreateDTO;
import com.zentra.server.dto.CategoryUpdateDTO;
import com.zentra.server.dto.EmployeeLoginDTO;
import com.zentra.server.service.RedisService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CategoryIntegrationTest extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RedisService redisService;

    // ==================== Create ====================
    @Test
    void createCategory_shouldPersistCategorySuccessfully() throws Exception {

        String categoryName = "Integration Test Category";

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

        JsonNode response = objectMapper.readTree(loginResponse);
        String jwt = response
                .path("data")
                .path("token")
                .asText();

        CategoryCreateDTO createRequest = new CategoryCreateDTO();
        createRequest.setName(categoryName);
        createRequest.setType(1);
        createRequest.setSort(99);
        createRequest.setDescription("Created by integration test");

        try {

            mockMvc.perform(
                            post("/category")
                                    .header("Authorization", "Bearer " + jwt)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(createRequest))
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(1));

            mockMvc.perform(
                            get("/category/list")
                                    .header("Authorization", "Bearer " + jwt)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(1))
                    .andExpect(jsonPath("$.data[*].name", hasItem(categoryName)));

        } finally {

            // Clean up the category
            mockMvc.perform(
                            get("/category/list")
                                    .header("Authorization", "Bearer " + jwt)
                    )
                    .andExpect(status().isOk());

            String listResponse = mockMvc.perform(
                            get("/category/list")
                                    .header("Authorization", "Bearer " + jwt)
                    )
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            JsonNode categories = objectMapper.readTree(listResponse).path("data");

            for (JsonNode category : categories) {
                if (categoryName.equals(category.path("name").asText())) {
                    Long categoryId = category.path("id").asLong();

                    mockMvc.perform(
                                    delete("/category/" + categoryId)
                                            .header("Authorization", "Bearer " + jwt)
                            )
                            .andExpect(status().isOk())
                            .andExpect(jsonPath("$.code").value(1));

                    break;
                }
            }
        }
    }

    // ==================== Get ====================
    @Test
    void getCategoryList_shouldCacheResultInRedis() throws Exception {

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

        JsonNode response = objectMapper.readTree(loginResponse);
        String jwt = response
                .path("data")
                .path("token")
                .asText();

        String cacheKey = RedisKeyConstants.CATEGORY_LIST_CACHE + 1L;

        try {
            redisService.delete(cacheKey);

            assertThat(redisService.exists(cacheKey)).isFalse();

            mockMvc.perform(
                            get("/category/list")
                                    .header("Authorization", "Bearer " + jwt)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(1));

            assertThat(redisService.exists(cacheKey)).isTrue();

            mockMvc.perform(
                            get("/category/list")
                                    .header("Authorization", "Bearer " + jwt)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(1));

        } finally {
            redisService.delete(cacheKey);
        }
    }

    // ==================== Update ====================
    @Test
    void updateCategory_shouldPersistUpdatedFields() throws Exception {
        String originalName = "Integration Update Category " + UUID.randomUUID();
        String updatedName = originalName + " Updated";

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
                .andReturn()
                .getResponse()
                .getContentAsString();

        String jwt = objectMapper.readTree(loginResponse)
                .path("data")
                .path("token")
                .asText();

        Long categoryId = null;

        try {
            CategoryCreateDTO createRequest = new CategoryCreateDTO();
            createRequest.setName(originalName);
            createRequest.setType(1);
            createRequest.setSort(10);
            createRequest.setDescription("Original description");

            mockMvc.perform(
                            post("/category")
                                    .header("Authorization", "Bearer " + jwt)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(createRequest))
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(1));

            String listResponse = mockMvc.perform(
                            get("/category/list")
                                    .header("Authorization", "Bearer " + jwt)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(1))
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            JsonNode categories = objectMapper.readTree(listResponse).path("data");

            for (JsonNode category : categories) {
                if (originalName.equals(category.path("name").asText())) {
                    categoryId = category.path("id").asLong();
                    break;
                }
            }

            assertThat(categoryId).isNotNull();

            CategoryUpdateDTO updateRequest = new CategoryUpdateDTO();
            updateRequest.setId(categoryId);
            updateRequest.setName(updatedName);
            updateRequest.setType(1);
            updateRequest.setStatus(1);
            updateRequest.setSort(20);
            updateRequest.setDescription("Updated description");

            mockMvc.perform(
                            patch("/category")
                                    .header("Authorization", "Bearer " + jwt)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(updateRequest))
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(1));

            String updatedListResponse = mockMvc.perform(
                            get("/category/list")
                                    .header("Authorization", "Bearer " + jwt)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(1))
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            JsonNode updatedCategories =
                    objectMapper.readTree(updatedListResponse).path("data");

            JsonNode updatedCategory = null;

            for (JsonNode category : updatedCategories) {
                if (categoryId.equals(category.path("id").asLong())) {
                    updatedCategory = category;
                    break;
                }
            }

            assertThat(updatedCategory).isNotNull();
            assertThat(updatedCategory.path("name").asText()).isEqualTo(updatedName);
            assertThat(updatedCategory.path("type").asInt()).isEqualTo(1);
            assertThat(updatedCategory.path("status").asInt()).isEqualTo(1);
            assertThat(updatedCategory.path("sort").asInt()).isEqualTo(20);
            assertThat(updatedCategory.path("description").asText())
                    .isEqualTo("Updated description");

        } finally {
            if (categoryId != null) {
                mockMvc.perform(
                        delete("/category/" + categoryId)
                                .header("Authorization", "Bearer " + jwt)
                );
            }

            redisService.delete(RedisKeyConstants.CATEGORY_LIST_CACHE + 1);
        }
    }

    // ==================== Delete ====================
    @Test
    void deleteCategory_shouldRejectCategoryWithDishes() throws Exception {

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

        JsonNode response = objectMapper.readTree(loginResponse);
        String jwt = response
                .path("data")
                .path("token")
                .asText();

        mockMvc.perform(
                        delete("/category/2")
                                .header("Authorization", "Bearer " + jwt)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ErrorCode.CATEGORY_HAS_DISHES));
    }

    @Test
    void deleteCategory_shouldDeleteCategoryAndEvictCache() throws Exception {

        String categoryName = "Integration Delete Category " + UUID.randomUUID();

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

        JsonNode loginResult = objectMapper.readTree(loginResponse);
        String jwt = loginResult
                .path("data")
                .path("token")
                .asText();

        CategoryCreateDTO createRequest = new CategoryCreateDTO();
        createRequest.setName(categoryName);
        createRequest.setType(1);
        createRequest.setSort(99);
        createRequest.setDescription("Created by integration test");

        try {

            // Create a temporary category
            mockMvc.perform(
                            post("/category")
                                    .header("Authorization", "Bearer " + jwt)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(createRequest))
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(1));

            // Find the generated category ID
            String listResponse = mockMvc.perform(
                            get("/category/list")
                                    .header("Authorization", "Bearer " + jwt)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(1))
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            JsonNode categories = objectMapper.readTree(listResponse).path("data");

            Long categoryId = null;

            for (JsonNode category : categories) {
                if (categoryName.equals(category.path("name").asText())) {
                    categoryId = category.path("id").asLong();
                    break;
                }
            }

            assertThat(categoryId).isNotNull();

            String cacheKey = RedisKeyConstants.CATEGORY_LIST_CACHE + 1;

            // Warm up the category list cache
            mockMvc.perform(
                            get("/category/list")
                                    .header("Authorization", "Bearer " + jwt)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(1));

            assertThat(redisService.exists(cacheKey)).isTrue();

            // Delete the category
            mockMvc.perform(
                            delete("/category/" + categoryId)
                                    .header("Authorization", "Bearer " + jwt)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(1));

            // Category deletion should evict the list cache
            assertThat(redisService.exists(cacheKey)).isFalse();

            // Verify the category is no longer returned
            String afterDeleteResponse = mockMvc.perform(
                            get("/category/list")
                                    .header("Authorization", "Bearer " + jwt)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(1))
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            JsonNode categoriesAfterDelete =
                    objectMapper.readTree(afterDeleteResponse).path("data");

            boolean categoryExists = false;

            for (JsonNode category : categoriesAfterDelete) {
                if (categoryName.equals(category.path("name").asText())) {
                    categoryExists = true;
                    break;
                }
            }

            assertThat(categoryExists).isFalse();

        } finally {

            // Clean up if the test failed before deletion
            String listResponse = mockMvc.perform(
                            get("/category/list")
                                    .header("Authorization", "Bearer " + jwt)
                    )
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            JsonNode categories = objectMapper.readTree(listResponse).path("data");

            for (JsonNode category : categories) {
                if (categoryName.equals(category.path("name").asText())) {

                    Long categoryId = category.path("id").asLong();

                    mockMvc.perform(
                            delete("/category/" + categoryId)
                                    .header("Authorization", "Bearer " + jwt)
                    );

                    break;
                }
            }

            redisService.delete(RedisKeyConstants.CATEGORY_LIST_CACHE + 1);
        }
    }
}