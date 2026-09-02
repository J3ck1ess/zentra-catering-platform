package com.zentra.server.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zentra.common.constant.ErrorCode;
import com.zentra.server.dto.EmployeeLoginDTO;
import com.zentra.server.entity.Employee;
import com.zentra.server.mapper.EmployeeMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import javax.crypto.SecretKey;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class EmployeeIntegrationTest extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EmployeeMapper employeeMapper;

    // ==================== Login ====================
    @Test
    void login_shouldReturnJwtForValidCredentials() throws Exception {

        EmployeeLoginDTO request = new EmployeeLoginDTO();
        request.setUsername("admin");
        request.setPassword("123456");

        mockMvc.perform(
                        post("/employee/login")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.msg").value("success"))
                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andExpect(jsonPath("$.data.userId").value(1));
    }

    @Test
    void login_shouldRejectWrongPassword() throws Exception {

        EmployeeLoginDTO request = new EmployeeLoginDTO();
        request.setUsername("admin");
        request.setPassword("wrong-password");

        mockMvc.perform(
                        post("/employee/login")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(40102));
    }

    @Test
    void login_shouldRejectNonexistentEmployee() throws Exception {

        EmployeeLoginDTO request = new EmployeeLoginDTO();
        request.setUsername("nonexistent");
        request.setPassword("123456");

        mockMvc.perform(
                        post("/employee/login")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(40102));
    }

    @Test
    void login_shouldRejectDisabledEmployee() throws Exception {

        Employee employee = new Employee();
        employee.setMerchantId(1L);
        employee.setUsername("disabled-test");
        employee.setPassword("$2a$10$77jOG3Q2WsQ4DY6.FW5L7uDj9NOkcUTTCgG.HuIDavgcryP4ASkV6");
        employee.setName("Disabled Test");
        employee.setRole("CASHIER");
        employee.setStatus(0);

        employeeMapper.insert(employee);

        try {
            EmployeeLoginDTO request = new EmployeeLoginDTO();
            request.setUsername("disabled-test");
            request.setPassword("123456");

            mockMvc.perform(
                            post("/employee/login")
                                    .contentType("application/json")
                                    .content(objectMapper.writeValueAsString(request))
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(40103));
        } finally {
            employeeMapper.deleteById(employee.getId(), 1L);
        }
    }

    // ==================== Get ====================
    @Test
    void getCurrentEmployee_shouldReturnEmployeeForValidToken() throws Exception {

        EmployeeLoginDTO loginRequest = new EmployeeLoginDTO();
        loginRequest.setUsername("admin");
        loginRequest.setPassword("123456");

        String token = mockMvc.perform(
                        post("/employee/login")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(loginRequest))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode response = objectMapper.readTree(token);
        String jwt = response
                .path("data")
                .path("token")
                .asText();

        mockMvc.perform(
                        get("/employee/me")
                                .header("Authorization", "Bearer " + jwt)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.username").value("admin"))
                .andExpect(jsonPath("$.data.name").value("Admin User"))
                .andExpect(jsonPath("$.data.role").value("SUPER_ADMIN"));
    }

    @Test
    void getCurrentEmployee_shouldRejectRequestWithoutToken() throws Exception {

        mockMvc.perform(
                        get("/employee/me")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ErrorCode.TOKEN_INVALID));
    }

    @Test
    void getCurrentEmployee_shouldRejectInvalidToken() throws Exception {

        mockMvc.perform(
                        get("/employee/me")
                                .header("Authorization", "Bearer invalid-token")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ErrorCode.TOKEN_INVALID));
    }

    @Test
    void getCurrentEmployee_shouldRejectExpiredToken() throws Exception {

        SecretKey key = Keys.hmacShaKeyFor(
                "zentra-secret-key-1234567890123456"
                        .getBytes(StandardCharsets.UTF_8)
        );

        String expiredToken = Jwts.builder()
                .claim("userId", 1L)
                .claim("merchantId", 1L)
                .claim("userType", "EMPLOYEE")
                .claim("role", "SUPER_ADMIN")
                .setExpiration(new Date(System.currentTimeMillis() - 1000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        mockMvc.perform(
                        get("/employee/me")
                                .header("Authorization", "Bearer " + expiredToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ErrorCode.TOKEN_EXPIRED));
    }

    // ==================== Delete ====================
    @Test
    void deleteEmployee_shouldRejectCashierWithoutPermission() throws Exception {

        EmployeeLoginDTO loginRequest = new EmployeeLoginDTO();
        loginRequest.setUsername("cashier");
        loginRequest.setPassword("123456");

        String loginResponse = mockMvc.perform(
                        post("/employee/login")
                                .contentType("application/json")
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
                        delete("/employee/1")
                                .header("Authorization", "Bearer " + jwt)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ErrorCode.NO_PERMISSION));
    }

}