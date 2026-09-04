package com.zentra.server.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zentra.common.constant.OrderStatus;
import com.zentra.common.constant.RedisKeyConstants;
import com.zentra.server.dto.EmployeeLoginDTO;
import com.zentra.server.entity.OrderItem;
import com.zentra.server.mapper.DishMapper;
import com.zentra.server.mapper.OrderItemMapper;
import com.zentra.server.mapper.OrderMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.zentra.server.entity.Order;

import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OrderIntegrationTest extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @BeforeEach
    void clearOrderIdempotencyKeys() {
        Set<String> keys = stringRedisTemplate.keys(
                RedisKeyConstants.ORDER_CREATE_IDEMPOTENCY + "*"
        );

        if (keys != null && !keys.isEmpty()) {
            stringRedisTemplate.delete(keys);
        }
    }

    // ==================== Create ====================
    @Test
    void createOrder_shouldPersistOrderAndItemsSuccessfully() throws Exception {
        EmployeeLoginDTO loginRequest = new EmployeeLoginDTO();
        loginRequest.setUsername("admin");
        loginRequest.setPassword("123456");

        String loginResponse = mockMvc.perform(
                        post("/employee/login")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(loginRequest))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = objectMapper.readTree(loginResponse)
                .path("data")
                .path("token")
                .asText();

        String requestJson = """
            {
                "items": [
                    {
                        "dishId": 2,
                        "quantity": 2
                    }
                ]
            }
            """;

        mockMvc.perform(
                        post("/order")
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json")
                                .content(requestJson)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1));

        List<Order> orders = orderMapper.findUserOrders(
                1L,
                null,
                0,
                100
        );

        Order createdOrder = orders.stream()
                .filter(order -> order.getUserId().equals(1L))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Created order was not persisted"));

        assertEquals(1L, createdOrder.getMerchantId());
        assertEquals(1L, createdOrder.getUserId());
        assertNotNull(createdOrder.getOrderNumber());
        assertFalse(createdOrder.getOrderNumber().isBlank());
        assertNotNull(createdOrder.getTotalAmount());
        assertEquals(1, createdOrder.getStatus());

        List<OrderItem> orderItems =
                orderItemMapper.findByOrderId(
                        createdOrder.getId(),
                        1L
                );

        assertEquals(1, orderItems.size());

        OrderItem item = orderItems.get(0);

        assertEquals(2L, item.getDishId());
        assertEquals(2, item.getQuantity());
        assertNotNull(item.getDishName());
        assertNotNull(item.getPrice());
        assertNotNull(item.getAmount());
    }

    @Test
    void createOrder_shouldRejectDuplicateRequest() throws Exception {
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

        String token = objectMapper.readTree(loginResponse)
                .path("data")
                .path("token")
                .asText();

        String requestJson = """
            {
                "items": [
                    {
                        "dishId": 2,
                        "quantity": 9
                    }
                ]
            }
            """;

        mockMvc.perform(
                        post("/order")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1));

        mockMvc.perform(
                        post("/order")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(52008));
    }

    // ==================== Get ====================
    @Test
    void getOrderDetail_shouldReturnOrderAndItemsSuccessfully() throws Exception {
        EmployeeLoginDTO loginRequest = new EmployeeLoginDTO();
        loginRequest.setUsername("admin");
        loginRequest.setPassword("123456");

        String loginResponse = mockMvc.perform(
                        post("/employee/login")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(loginRequest))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = objectMapper.readTree(loginResponse)
                .path("data")
                .path("token")
                .asText();

        String requestJson = """
            {
                "items": [
                    {
                        "dishId": 2,
                        "quantity": 2
                    },
                    {
                        "dishId": 3,
                        "quantity": 1
                    }
                ]
            }
            """;

        mockMvc.perform(
                        post("/order")
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json")
                                .content(requestJson)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1));

        List<Order> orders = orderMapper.findUserOrders(
                1L,
                null,
                0,
                100
        );

        Order createdOrder = orders.stream()
                .filter(order -> order.getUserId().equals(1L))
                .findFirst()
                .orElseThrow(() ->
                        new AssertionError("Created order was not found")
                );

        List<OrderItem> orderItems =
                orderItemMapper.findByOrderId(
                        createdOrder.getId(),
                        1L
                );

        assertEquals(2, orderItems.size());

        mockMvc.perform(
                        get("/order/{id}", createdOrder.getId())
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.data.id")
                        .value(createdOrder.getId().intValue()))
                .andExpect(jsonPath("$.data.orderNumber")
                        .value(createdOrder.getOrderNumber()))
                .andExpect(jsonPath("$.data.totalAmount")
                        .value(createdOrder.getTotalAmount().doubleValue()))
                .andExpect(jsonPath("$.data.status")
                        .value(createdOrder.getStatus()))
                .andExpect(jsonPath("$.data.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.data.items").isArray())
                .andExpect(jsonPath("$.data.items.length()").value(2));
    }

    // ==================== Page ====================
    @Test
    void pageOrders_shouldReturnCreatedOrder() throws Exception {
        EmployeeLoginDTO loginRequest = new EmployeeLoginDTO();
        loginRequest.setUsername("admin");
        loginRequest.setPassword("123456");

        String loginResponse = mockMvc.perform(
                        post("/employee/login")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(loginRequest))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = objectMapper.readTree(loginResponse)
                .path("data")
                .path("token")
                .asText();

        String requestJson = """
            {
                "items": [
                    {
                        "dishId": 2,
                        "quantity": 6
                    }
                ]
            }
            """;

        mockMvc.perform(
                        post("/order")
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json")
                                .content(requestJson)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1));

        List<Order> orders = orderMapper.findUserOrders(
                1L,
                null,
                0,
                100
        );

        Order createdOrder = orders.stream()
                .filter(order -> order.getUserId().equals(1L))
                .findFirst()
                .orElseThrow(() ->
                        new AssertionError("Created order was not found")
                );

        mockMvc.perform(
                        get("/order")
                                .header("Authorization", "Bearer " + token)
                                .param("page", "1")
                                .param("pageSize", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.records").isArray())
                .andExpect(jsonPath("$.data.records").isNotEmpty())
                .andExpect(
                        jsonPath("$.data.records[*].id")
                                .value(
                                        hasItem(
                                                createdOrder.getId().intValue()
                                        )
                                )
                );
    }

    // ==================== Update ====================
    @Test
    void updateOrderStatus_shouldPersistValidTransition() throws Exception {
        EmployeeLoginDTO loginRequest = new EmployeeLoginDTO();
        loginRequest.setUsername("admin");
        loginRequest.setPassword("123456");

        String loginResponse = mockMvc.perform(
                        post("/employee/login")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(loginRequest))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = objectMapper.readTree(loginResponse)
                .path("data")
                .path("token")
                .asText();

        String requestJson = """
            {
                "items": [
                    {
                        "dishId": 2,
                        "quantity": 4
                    }
                ]
            }
            """;

        mockMvc.perform(
                        post("/order")
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json")
                                .content(requestJson)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1));

        List<Order> orders = orderMapper.findUserOrders(
                1L,
                null,
                0,
                100
        );

        Order createdOrder = orders.stream()
                .filter(order ->
                        order.getStatus().equals(OrderStatus.PENDING)
                )
                .findFirst()
                .orElseThrow(() ->
                        new AssertionError("Pending order was not found")
                );

        assertEquals(
                OrderStatus.PENDING,
                createdOrder.getStatus()
        );

        mockMvc.perform(
                        patch("/order/{id}/status", createdOrder.getId())
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json")
                                .content("""
                                    {
                                        "status": 2
                                    }
                                    """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1));

        Order updatedOrder =
                orderMapper.findById(
                        createdOrder.getId(),
                        1L
                );

        assertNotNull(updatedOrder);
        assertEquals(
                OrderStatus.PAID,
                updatedOrder.getStatus()
        );
    }

}