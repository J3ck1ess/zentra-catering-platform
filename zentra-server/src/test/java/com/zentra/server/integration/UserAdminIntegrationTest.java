package com.zentra.server.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zentra.common.constant.ErrorCode;
import com.zentra.server.dto.EmployeeLoginDTO;
import com.zentra.server.entity.User;
import com.zentra.server.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserAdminIntegrationTest extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserMapper userMapper;

    // ==================== Page ====================
    @Test
    void pageUsers_shouldReturnUsersSuccessfully() throws Exception {
        String token = loginAsAdmin();

        mockMvc.perform(
                        get("/admin/users")
                                .header("Authorization", "Bearer " + token)
                                .param("page", "1")
                                .param("pageSize", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.data.total").isNumber())
                .andExpect(jsonPath("$.data.records").isArray());
    }

    @Test
    void pageUsers_shouldFilterByUsername() throws Exception {
        String token = loginAsAdmin();

        User existingUser = userMapper.findById(2L, 1L);

        assertThat(existingUser).isNotNull();

        mockMvc.perform(
                        get("/admin/users")
                                .header("Authorization", "Bearer " + token)
                                .param("page", "1")
                                .param("pageSize", "10")
                                .param("username", existingUser.getUsername())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.records[0].username")
                        .value(existingUser.getUsername()));
    }

    @Test
    void pageUsers_shouldFilterByStatus() throws Exception {
        String token = loginAsAdmin();

        mockMvc.perform(
                        get("/admin/users")
                                .header("Authorization", "Bearer " + token)
                                .param("page", "1")
                                .param("pageSize", "10")
                                .param("status", "1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.data.total").isNumber())
                .andExpect(jsonPath("$.data.records").isArray());
    }

    // ==================== Update ====================
    @Test
    void updateUserStatus_shouldPersistChangesSuccessfully() throws Exception {
        String token = loginAsAdmin();

        Long userId = 2L;

        User originalUser = userMapper.findByIdOnly(userId);

        assertThat(originalUser).isNotNull();

        try {
            mockMvc.perform(
                            patch("/admin/users/{id}/status", userId)
                                    .header("Authorization", "Bearer " + token)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content("""
                                            {"status": 0}
                                            """)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(1));

            User updatedUser = userMapper.findByIdOnly(userId);

            assertThat(updatedUser).isNotNull();
            assertThat(updatedUser.getStatus()).isEqualTo(0);
        } finally {
            userMapper.update(originalUser);
        }
    }

    @Test
    void updateUserStatus_shouldRejectInvalidStatus() throws Exception {
        String token = loginAsAdmin();

        mockMvc.perform(
                        patch("/admin/users/{id}/status", 2L)
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {"status": 2}
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code")
                        .value(ErrorCode.USER_STATUS_INVALID));
    }

    // ==================== Permission ====================
    @Test
    void pageUsers_shouldRejectKitchenStaffWithoutPermission() throws Exception {
        String token = loginAsKitchenStaff();

        mockMvc.perform(
                        get("/admin/users")
                                .header("Authorization", "Bearer " + token)
                                .param("page", "1")
                                .param("pageSize", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code")
                        .value(ErrorCode.NO_PERMISSION));
    }

    @Test
    void updateUserStatus_shouldRejectStoreManagerWithoutPermission() throws Exception {
        String token = loginAsStoreManager();

        mockMvc.perform(
                        patch("/admin/users/{id}/status", 2L)
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {"status": 0}
                                    """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code")
                        .value(ErrorCode.NO_PERMISSION));
    }

    // ==================== Authentication ====================
    private String loginAsAdmin() throws Exception {
        return login("admin", "123456");
    }

    private String loginAsKitchenStaff() throws Exception {
        return login("kitchen", "123456");
    }

    private String loginAsStoreManager() throws Exception {
        return login("manager", "123456");
    }

    private String login(String username, String password) throws Exception {
        EmployeeLoginDTO loginRequest = new EmployeeLoginDTO();
        loginRequest.setUsername(username);
        loginRequest.setPassword(password);

        String response = mockMvc.perform(
                        post("/employee/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(response)
                .get("data")
                .get("token")
                .asText();
    }
}