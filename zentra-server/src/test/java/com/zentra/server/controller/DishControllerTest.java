package com.zentra.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zentra.common.constant.ErrorCode;
import com.zentra.common.constant.ErrorMessage;
import com.zentra.common.exception.BusinessException;
import com.zentra.common.result.PageResult;
import com.zentra.server.dto.DishCreateDTO;
import com.zentra.server.dto.DishDTO;
import com.zentra.server.dto.DishQueryDTO;
import com.zentra.server.dto.DishUpdateDTO;
import com.zentra.server.exception.GlobalExceptionHandler;
import com.zentra.server.service.DishService;
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
class DishControllerTest {

    @Mock
    private DishService dishService;

    @InjectMocks
    private DishController dishController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {

        mockMvc = MockMvcBuilders
                .standaloneSetup(dishController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
    }

    // ==================== Create ====================
    @Test
    void createDish_shouldCreateDishSuccessfully()
            throws Exception {

        DishCreateDTO request = new DishCreateDTO();
        request.setName("Cheese Burger");
        request.setPrice(new BigDecimal("12.99"));
        request.setCategoryId(1L);
        request.setStatus(1);

        doNothing().when(dishService)
                .create(any(DishCreateDTO.class));

        mockMvc.perform(
                        post("/dish")
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

        ArgumentCaptor<DishCreateDTO> captor =
                ArgumentCaptor.forClass(DishCreateDTO.class);

        verify(dishService).create(captor.capture());

        DishCreateDTO captured = captor.getValue();

        assertThat(captured.getName())
                .isEqualTo("Cheese Burger");
        assertThat(captured.getPrice())
                .isEqualByComparingTo("12.99");
        assertThat(captured.getCategoryId())
                .isEqualTo(1L);
        assertThat(captured.getStatus())
                .isEqualTo(1);
    }

    @Test
    void createDish_shouldRejectBlankName()
            throws Exception {

        DishCreateDTO request = new DishCreateDTO();
        request.setName("");
        request.setPrice(new BigDecimal("12.99"));
        request.setCategoryId(1L);

        mockMvc.perform(
                        post("/dish")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code")
                        .value(ErrorCode.FAILURE))
                .andExpect(jsonPath("$.msg")
                        .value("name cannot be blank"));

        verifyNoInteractions(dishService);
    }

    @Test
    void createDish_shouldRejectRequestWithoutPrice()
            throws Exception {

        DishCreateDTO request = new DishCreateDTO();
        request.setName("Cheese Burger");
        request.setCategoryId(1L);

        mockMvc.perform(
                        post("/dish")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code")
                        .value(ErrorCode.FAILURE))
                .andExpect(jsonPath("$.msg")
                        .value("price cannot be null"));

        verifyNoInteractions(dishService);
    }

    @Test
    void createDish_shouldRejectPriceBelowMinimum()
            throws Exception {

        DishCreateDTO request = new DishCreateDTO();
        request.setName("Cheese Burger");
        request.setPrice(new BigDecimal("0.00"));
        request.setCategoryId(1L);

        mockMvc.perform(
                        post("/dish")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code")
                        .value(ErrorCode.FAILURE))
                .andExpect(jsonPath("$.msg")
                        .value("price must be greater than 0.01"));

        verifyNoInteractions(dishService);
    }

    @Test
    void createDish_shouldRejectRequestWithoutCategoryId()
            throws Exception {

        DishCreateDTO request = new DishCreateDTO();
        request.setName("Cheese Burger");
        request.setPrice(new BigDecimal("12.99"));

        mockMvc.perform(
                        post("/dish")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code")
                        .value(ErrorCode.FAILURE))
                .andExpect(jsonPath("$.msg")
                        .value("categoryId cannot be null"));

        verifyNoInteractions(dishService);
    }

    @Test
    void createDish_shouldReturnCategoryNotFound()
            throws Exception {

        DishCreateDTO request = new DishCreateDTO();
        request.setName("Cheese Burger");
        request.setPrice(new BigDecimal("12.99"));
        request.setCategoryId(999L);

        doThrow(new BusinessException(
                ErrorCode.CATEGORY_NOT_FOUND,
                ErrorMessage.CATEGORY_NOT_FOUND
        )).when(dishService)
                .create(any(DishCreateDTO.class));

        mockMvc.perform(
                        post("/dish")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code")
                        .value(ErrorCode.CATEGORY_NOT_FOUND))
                .andExpect(jsonPath("$.msg")
                        .value(ErrorMessage.CATEGORY_NOT_FOUND));

        verify(dishService)
                .create(any(DishCreateDTO.class));
    }

    // ==================== Page ====================
    @Test
    void page_shouldReturnDishListSuccessfully()
            throws Exception {

        DishDTO dish = new DishDTO();
        dish.setId(1L);
        dish.setName("Cheese Burger");
        dish.setPrice(new BigDecimal("12.99"));
        dish.setCategoryId(1L);
        dish.setStatus(1);

        PageResult<DishDTO> pageResult =
                new PageResult<>(
                        1L,
                        List.of(dish)
                );

        when(dishService.page(any(DishQueryDTO.class)))
                .thenReturn(pageResult);

        mockMvc.perform(
                        get("/dish")
                                .param("page", "1")
                                .param("pageSize", "10")
                                .param("name", "Burger")
                                .param("categoryId", "1")
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
                        .value(1))
                .andExpect(jsonPath("$.data.records[0].name")
                        .value("Cheese Burger"))
                .andExpect(jsonPath("$.data.records[0].price")
                        .value(12.99))
                .andExpect(jsonPath("$.data.records[0].categoryId")
                        .value(1))
                .andExpect(jsonPath("$.data.records[0].status")
                        .value(1));

        ArgumentCaptor<DishQueryDTO> captor =
                ArgumentCaptor.forClass(DishQueryDTO.class);

        verify(dishService).page(captor.capture());

        DishQueryDTO captured = captor.getValue();

        assertThat(captured.getPage())
                .isEqualTo(1);
        assertThat(captured.getPageSize())
                .isEqualTo(10);
        assertThat(captured.getName())
                .isEqualTo("Burger");
        assertThat(captured.getCategoryId())
                .isEqualTo(1L);
        assertThat(captured.getStatus())
                .isEqualTo(1);
    }

    // ==================== List ====================
    @Test
    void list_shouldReturnEnabledDishesSuccessfully()
            throws Exception {

        DishDTO dish = new DishDTO();
        dish.setId(1L);
        dish.setName("Cheese Burger");
        dish.setPrice(new BigDecimal("12.99"));
        dish.setCategoryId(1L);
        dish.setStatus(1);

        when(dishService.list(1L))
                .thenReturn(List.of(dish));

        mockMvc.perform(
                        get("/dish/list")
                                .param("categoryId", "1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code")
                        .value(1))
                .andExpect(jsonPath("$.msg")
                        .value("success"))
                .andExpect(jsonPath("$.data[0].id")
                        .value(1))
                .andExpect(jsonPath("$.data[0].name")
                        .value("Cheese Burger"))
                .andExpect(jsonPath("$.data[0].price")
                        .value(12.99))
                .andExpect(jsonPath("$.data[0].categoryId")
                        .value(1))
                .andExpect(jsonPath("$.data[0].status")
                        .value(1));

        verify(dishService)
                .list(1L);
    }

    // ==================== Get By ID ====================
    @Test
    void getById_shouldReturnDishSuccessfully()
            throws Exception {

        DishDTO dish = new DishDTO();
        dish.setId(1L);
        dish.setName("Cheese Burger");
        dish.setPrice(new BigDecimal("12.99"));
        dish.setCategoryId(1L);
        dish.setStatus(1);

        when(dishService.getById(1L))
                .thenReturn(dish);

        mockMvc.perform(
                        get("/dish/{id}", 1L)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code")
                        .value(1))
                .andExpect(jsonPath("$.msg")
                        .value("success"))
                .andExpect(jsonPath("$.data.id")
                        .value(1))
                .andExpect(jsonPath("$.data.name")
                        .value("Cheese Burger"))
                .andExpect(jsonPath("$.data.price")
                        .value(12.99))
                .andExpect(jsonPath("$.data.categoryId")
                        .value(1))
                .andExpect(jsonPath("$.data.status")
                        .value(1));

        verify(dishService)
                .getById(1L);
    }

    @Test
    void getById_shouldReturnNotFoundWhenDishDoesNotExist()
            throws Exception {

        when(dishService.getById(999L))
                .thenThrow(new BusinessException(
                        ErrorCode.DISH_NOT_FOUND,
                        ErrorMessage.DISH_NOT_FOUND
                ));

        mockMvc.perform(
                        get("/dish/{id}", 999L)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code")
                        .value(ErrorCode.DISH_NOT_FOUND))
                .andExpect(jsonPath("$.msg")
                        .value(ErrorMessage.DISH_NOT_FOUND));

        verify(dishService)
                .getById(999L);
    }

    // ==================== Update ====================
    @Test
    void updateDish_shouldUpdateDishSuccessfully()
            throws Exception {

        DishUpdateDTO request = new DishUpdateDTO();
        request.setId(1L);
        request.setName("Cheese Burger");
        request.setPrice(new BigDecimal("12.99"));
        request.setCategoryId(1L);
        request.setStatus(1);

        doNothing().when(dishService)
                .update(any(DishUpdateDTO.class));

        mockMvc.perform(
                        patch("/dish")
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

        ArgumentCaptor<DishUpdateDTO> captor =
                ArgumentCaptor.forClass(DishUpdateDTO.class);

        verify(dishService).update(captor.capture());

        DishUpdateDTO captured = captor.getValue();

        assertThat(captured.getId())
                .isEqualTo(1L);
        assertThat(captured.getName())
                .isEqualTo("Cheese Burger");
        assertThat(captured.getPrice())
                .isEqualByComparingTo("12.99");
        assertThat(captured.getCategoryId())
                .isEqualTo(1L);
        assertThat(captured.getStatus())
                .isEqualTo(1);
    }

    @Test
    void updateDish_shouldRejectRequestWithoutId()
            throws Exception {

        DishUpdateDTO request = new DishUpdateDTO();
        request.setName("Cheese Burger");
        request.setPrice(new BigDecimal("12.99"));
        request.setCategoryId(1L);
        request.setStatus(1);

        mockMvc.perform(
                        patch("/dish")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code")
                        .value(ErrorCode.FAILURE))
                .andExpect(jsonPath("$.msg")
                        .value("dish id cannot be null"));

        verifyNoInteractions(dishService);
    }

    @Test
    void updateDish_shouldRejectEmptyName()
            throws Exception {

        DishUpdateDTO request = new DishUpdateDTO();
        request.setId(1L);
        request.setName("");
        request.setPrice(new BigDecimal("12.99"));
        request.setCategoryId(1L);
        request.setStatus(1);

        mockMvc.perform(
                        patch("/dish")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code")
                        .value(ErrorCode.FAILURE))
                .andExpect(jsonPath("$.msg")
                        .value("name cannot be empty"));

        verifyNoInteractions(dishService);
    }

    @Test
    void updateDish_shouldRejectPriceBelowMinimum()
            throws Exception {

        DishUpdateDTO request = new DishUpdateDTO();
        request.setId(1L);
        request.setName("Cheese Burger");
        request.setPrice(new BigDecimal("0.00"));
        request.setCategoryId(1L);
        request.setStatus(1);

        mockMvc.perform(
                        patch("/dish")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code")
                        .value(ErrorCode.FAILURE))
                .andExpect(jsonPath("$.msg")
                        .value("price must be greater than 0.01"));

        verifyNoInteractions(dishService);
    }

    @Test
    void updateDish_shouldReturnNotFoundWhenDishDoesNotExist()
            throws Exception {

        DishUpdateDTO request = new DishUpdateDTO();
        request.setId(999L);
        request.setName("Cheese Burger");

        doThrow(new BusinessException(
                ErrorCode.DISH_NOT_FOUND,
                ErrorMessage.DISH_NOT_FOUND
        )).when(dishService)
                .update(any(DishUpdateDTO.class));

        mockMvc.perform(
                        patch("/dish")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code")
                        .value(ErrorCode.DISH_NOT_FOUND))
                .andExpect(jsonPath("$.msg")
                        .value(ErrorMessage.DISH_NOT_FOUND));

        verify(dishService)
                .update(any(DishUpdateDTO.class));
    }

    // ==================== Delete ====================
    @Test
    void deleteDish_shouldDeleteDishSuccessfully()
            throws Exception {

        Long dishId = 1L;

        doNothing().when(dishService)
                .deleteById(dishId);

        mockMvc.perform(
                        delete("/dish/{id}", dishId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code")
                        .value(1))
                .andExpect(jsonPath("$.msg")
                        .value("success"));

        verify(dishService)
                .deleteById(dishId);
    }

    @Test
    void deleteDish_shouldReturnNotFoundWhenDishDoesNotExist()
            throws Exception {

        Long dishId = 999L;

        doThrow(new BusinessException(
                ErrorCode.DISH_NOT_FOUND,
                ErrorMessage.DISH_NOT_FOUND
        )).when(dishService)
                .deleteById(dishId);

        mockMvc.perform(
                        delete("/dish/{id}", dishId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code")
                        .value(ErrorCode.DISH_NOT_FOUND))
                .andExpect(jsonPath("$.msg")
                        .value(ErrorMessage.DISH_NOT_FOUND));

        verify(dishService)
                .deleteById(dishId);
    }
}