package com.zentra.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zentra.common.constant.ErrorCode;
import com.zentra.common.constant.ErrorMessage;
import com.zentra.common.exception.BusinessException;
import com.zentra.common.result.PageResult;
import com.zentra.server.dto.*;
import com.zentra.server.exception.GlobalExceptionHandler;
import com.zentra.server.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {

        mockMvc = MockMvcBuilders
                .standaloneSetup(orderController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
    }

    // ==================== Create ====================
    @Test
    void create_shouldCreateOrderSuccessfully()
            throws Exception {

        OrderItemCreateDTO item = new OrderItemCreateDTO();
        item.setDishId(1L);
        item.setQuantity(2);

        OrderCreateDTO request = new OrderCreateDTO();
        request.setItems(List.of(item));

        doNothing().when(orderService)
                .create(any(OrderCreateDTO.class));

        mockMvc.perform(
                        post("/order")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code")
                        .value(1))
                .andExpect(jsonPath("$.msg")
                        .value("success"));

        ArgumentCaptor<OrderCreateDTO> captor =
                ArgumentCaptor.forClass(OrderCreateDTO.class);

        verify(orderService)
                .create(captor.capture());

        OrderCreateDTO captured = captor.getValue();

        assertThat(captured.getItems())
                .hasSize(1);

        assertThat(captured.getItems().get(0).getDishId())
                .isEqualTo(1L);

        assertThat(captured.getItems().get(0).getQuantity())
                .isEqualTo(2);
    }

    @Test
    void create_shouldRejectEmptyOrderItems()
            throws Exception {

        OrderCreateDTO request = new OrderCreateDTO();
        request.setItems(List.of());

        mockMvc.perform(
                        post("/order")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code")
                        .value(ErrorCode.FAILURE))
                .andExpect(jsonPath("$.msg")
                        .value("order items cannot be empty"));

        verifyNoInteractions(orderService);
    }

    @Test
    void create_shouldRejectOrderItemWithInvalidQuantity()
            throws Exception {

        OrderItemCreateDTO item = new OrderItemCreateDTO();
        item.setDishId(1L);
        item.setQuantity(0);

        OrderCreateDTO request = new OrderCreateDTO();
        request.setItems(List.of(item));

        mockMvc.perform(
                        post("/order")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code")
                        .value(ErrorCode.FAILURE))
                .andExpect(jsonPath("$.msg")
                        .value("quantity must be greater than or equal to 1"));

        verifyNoInteractions(orderService);
    }

    @Test
    void create_shouldReturnBadRequestWhenDuplicateRequestDetected()
            throws Exception {

        OrderItemCreateDTO item = new OrderItemCreateDTO();
        item.setDishId(1L);
        item.setQuantity(2);

        OrderCreateDTO request = new OrderCreateDTO();
        request.setItems(List.of(item));

        doThrow(new BusinessException(
                ErrorCode.DUPLICATE_ORDER_REQUEST,
                ErrorMessage.DUPLICATE_ORDER_REQUEST
        )).when(orderService)
                .create(any(OrderCreateDTO.class));

        mockMvc.perform(
                        post("/order")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code")
                        .value(ErrorCode.DUPLICATE_ORDER_REQUEST))
                .andExpect(jsonPath("$.msg")
                        .value(ErrorMessage.DUPLICATE_ORDER_REQUEST));

        verify(orderService)
                .create(any(OrderCreateDTO.class));
    }

    // ==================== Page ====================
    @Test
    void page_shouldReturnOrdersSuccessfully()
            throws Exception {

        OrderPageDTO order = new OrderPageDTO();
        order.setId(1L);

        PageResult<OrderPageDTO> pageResult =
                new PageResult<>(
                        1L,
                        List.of(order)
                );

        when(orderService.page(any(OrderQueryDTO.class)))
                .thenReturn(pageResult);

        mockMvc.perform(
                        get("/order")
                                .param("page", "1")
                                .param("pageSize", "10")
                                .param("status", "1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code")
                        .value(1))
                .andExpect(jsonPath("$.msg")
                        .value("success"))
                .andExpect(jsonPath("$.data.total")
                        .value(1))
                .andExpect(jsonPath("$.data.records[0].id")
                        .value(1));

        ArgumentCaptor<OrderQueryDTO> captor =
                ArgumentCaptor.forClass(OrderQueryDTO.class);

        verify(orderService)
                .page(captor.capture());

        OrderQueryDTO captured = captor.getValue();

        assertThat(captured.getPage())
                .isEqualTo(1);

        assertThat(captured.getPageSize())
                .isEqualTo(10);

        assertThat(captured.getStatus())
                .isEqualTo(1);
    }

    @Test
    void page_shouldRejectInvalidPage()
            throws Exception {

        mockMvc.perform(
                        get("/order")
                                .param("page", "0")
                                .param("pageSize", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code")
                        .value(ErrorCode.FAILURE))
                .andExpect(jsonPath("$.msg")
                        .value("page must be greater than or equal to 1"));

        verifyNoInteractions(orderService);
    }

    // ==================== Get By ID ====================
    @Test
    void getById_shouldReturnOrderDetailSuccessfully()
            throws Exception {

        OrderItemDTO item = new OrderItemDTO();
        item.setDishId(1L);
        item.setDishName("Pizza");
        item.setPrice(new BigDecimal("10.99"));
        item.setQuantity(2);
        item.setAmount(new BigDecimal("21.98"));

        OrderDetailDTO order = new OrderDetailDTO();
        order.setId(1L);
        order.setOrderNumber("1746628823000");
        order.setTotalAmount(new BigDecimal("21.98"));
        order.setStatus(1);
        order.setItems(List.of(item));

        when(orderService.getById(1L))
                .thenReturn(order);

        mockMvc.perform(
                        get("/order/{id}", 1L)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code")
                        .value(1))
                .andExpect(jsonPath("$.msg")
                        .value("success"))
                .andExpect(jsonPath("$.data.id")
                        .value(1))
                .andExpect(jsonPath("$.data.orderNumber")
                        .value("1746628823000"))
                .andExpect(jsonPath("$.data.totalAmount")
                        .value(21.98))
                .andExpect(jsonPath("$.data.status")
                        .value(1))
                .andExpect(jsonPath("$.data.items[0].dishId")
                        .value(1))
                .andExpect(jsonPath("$.data.items[0].dishName")
                        .value("Pizza"))
                .andExpect(jsonPath("$.data.items[0].price")
                        .value(10.99))
                .andExpect(jsonPath("$.data.items[0].quantity")
                        .value(2))
                .andExpect(jsonPath("$.data.items[0].amount")
                        .value(21.98));

        verify(orderService)
                .getById(1L);
    }

    @Test
    void getById_shouldReturnNotFoundWhenOrderDoesNotExist()
            throws Exception {

        Long orderId = 999L;

        when(orderService.getById(orderId))
                .thenThrow(new BusinessException(
                        ErrorCode.ORDER_NOT_FOUND,
                        ErrorMessage.ORDER_NOT_FOUND
                ));

        mockMvc.perform(
                        get("/order/{id}", orderId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code")
                        .value(ErrorCode.ORDER_NOT_FOUND))
                .andExpect(jsonPath("$.msg")
                        .value(ErrorMessage.ORDER_NOT_FOUND));

        verify(orderService)
                .getById(orderId);
    }

    // ==================== Update ====================
    @Test
    void updateStatus_shouldUpdateOrderStatusSuccessfully()
            throws Exception {

        Long orderId = 1L;

        OrderStatusUpdateDTO request =
                new OrderStatusUpdateDTO();

        request.setStatus(2);

        doNothing().when(orderService)
                .updateStatus(orderId, 2);

        mockMvc.perform(
                        patch(
                                "/order/{id}/status",
                                orderId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                request
                                        )
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code")
                        .value(1))
                .andExpect(jsonPath("$.msg")
                        .value("success"));

        verify(orderService)
                .updateStatus(orderId, 2);
    }

    @Test
    void updateStatus_shouldRejectNullStatus()
            throws Exception {

        OrderStatusUpdateDTO request =
                new OrderStatusUpdateDTO();

        mockMvc.perform(
                        patch(
                                "/order/{id}/status",
                                1L
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                request
                                        )
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code")
                        .value(ErrorCode.FAILURE))
                .andExpect(jsonPath("$.msg")
                        .value("status cannot be null"));

        verifyNoInteractions(orderService);
    }

    @Test
    void updateStatus_shouldReturnBadRequestWhenStatusIsInvalid()
            throws Exception {

        Long orderId = 1L;

        OrderStatusUpdateDTO request =
                new OrderStatusUpdateDTO();

        request.setStatus(999);

        doThrow(new BusinessException(
                ErrorCode.ORDER_STATUS_INVALID,
                ErrorMessage.ORDER_STATUS_INVALID
        )).when(orderService)
                .updateStatus(orderId, 999);

        mockMvc.perform(
                        patch(
                                "/order/{id}/status",
                                orderId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                request
                                        )
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code")
                        .value(ErrorCode.ORDER_STATUS_INVALID))
                .andExpect(jsonPath("$.msg")
                        .value(ErrorMessage.ORDER_STATUS_INVALID));

        verify(orderService)
                .updateStatus(orderId, 999);
    }
}