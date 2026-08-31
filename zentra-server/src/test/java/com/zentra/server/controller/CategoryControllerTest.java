package com.zentra.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zentra.common.constant.ErrorCode;
import com.zentra.common.constant.ErrorMessage;
import com.zentra.common.exception.BusinessException;
import com.zentra.common.result.PageResult;
import com.zentra.server.dto.CategoryCreateDTO;
import com.zentra.server.dto.CategoryDTO;
import com.zentra.server.dto.CategoryQueryDTO;
import com.zentra.server.dto.CategoryUpdateDTO;
import com.zentra.server.exception.GlobalExceptionHandler;
import com.zentra.server.service.CategoryService;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {

        mockMvc = MockMvcBuilders
                .standaloneSetup(categoryController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
    }

    // ==================== Create ====================
    @Test
    void createCategory_shouldCreateCategorySuccessfully()
            throws Exception {

        CategoryCreateDTO request = new CategoryCreateDTO();
        request.setName("Food");
        request.setType(1);
        request.setStatus(1);
        request.setSort(10);
        request.setDescription("Hot dishes");

        doNothing().when(categoryService)
                .create(any(CategoryCreateDTO.class));

        mockMvc.perform(
                        post("/category")
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

        ArgumentCaptor<CategoryCreateDTO> captor =
                ArgumentCaptor.forClass(CategoryCreateDTO.class);

        verify(categoryService).create(captor.capture());

        CategoryCreateDTO captured = captor.getValue();

        assertThat(captured.getName())
                .isEqualTo("Food");
        assertThat(captured.getType())
                .isEqualTo(1);
        assertThat(captured.getStatus())
                .isEqualTo(1);
        assertThat(captured.getSort())
                .isEqualTo(10);
        assertThat(captured.getDescription())
                .isEqualTo("Hot dishes");
    }

    @Test
    void createCategory_shouldRejectBlankName()
            throws Exception {

        CategoryCreateDTO request = new CategoryCreateDTO();
        request.setName("");
        request.setType(1);

        mockMvc.perform(
                        post("/category")
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

        verifyNoInteractions(categoryService);
    }

    @Test
    void createCategory_shouldRejectRequestWithoutType()
            throws Exception {

        CategoryCreateDTO request = new CategoryCreateDTO();
        request.setName("Food");

        mockMvc.perform(
                        post("/category")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code")
                        .value(ErrorCode.FAILURE))
                .andExpect(jsonPath("$.msg")
                        .value("type cannot be null"));

        verifyNoInteractions(categoryService);
    }

    @Test
    void createCategory_shouldReturnConflictWhenNameAlreadyExists()
            throws Exception {

        CategoryCreateDTO request = new CategoryCreateDTO();
        request.setName("Food");
        request.setType(1);

        doThrow(new BusinessException(
                ErrorCode.CATEGORY_NAME_ALREADY_EXISTS,
                ErrorMessage.CATEGORY_NAME_ALREADY_EXISTS
        )).when(categoryService)
                .create(any(CategoryCreateDTO.class));

        mockMvc.perform(
                        post("/category")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code")
                        .value(ErrorCode.CATEGORY_NAME_ALREADY_EXISTS))
                .andExpect(jsonPath("$.msg")
                        .value(ErrorMessage.CATEGORY_NAME_ALREADY_EXISTS));

        verify(categoryService)
                .create(any(CategoryCreateDTO.class));
    }

    // ==================== Page ====================
    @Test
    void page_shouldReturnCategoryListSuccessfully()
            throws Exception {

        CategoryDTO category = new CategoryDTO();
        category.setId(1L);
        category.setName("Food");
        category.setType(1);
        category.setStatus(1);
        category.setSort(10);
        category.setDescription("Hot dishes");

        PageResult<CategoryDTO> pageResult =
                new PageResult<>(
                        1L,
                        List.of(category)
                );

        when(categoryService.page(any(CategoryQueryDTO.class)))
                .thenReturn(pageResult);

        mockMvc.perform(
                        get("/category")
                                .param("page", "1")
                                .param("pageSize", "10")
                                .param("name", "Food")
                                .param("type", "1")
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
                        .value("Food"))
                .andExpect(jsonPath("$.data.records[0].type")
                        .value(1))
                .andExpect(jsonPath("$.data.records[0].status")
                        .value(1))
                .andExpect(jsonPath("$.data.records[0].sort")
                        .value(10))
                .andExpect(jsonPath("$.data.records[0].description")
                        .value("Hot dishes"));

        ArgumentCaptor<CategoryQueryDTO> captor =
                ArgumentCaptor.forClass(CategoryQueryDTO.class);

        verify(categoryService).page(captor.capture());

        CategoryQueryDTO captured = captor.getValue();

        assertThat(captured.getPage())
                .isEqualTo(1);
        assertThat(captured.getPageSize())
                .isEqualTo(10);
        assertThat(captured.getName())
                .isEqualTo("Food");
        assertThat(captured.getType())
                .isEqualTo(1);
        assertThat(captured.getStatus())
                .isEqualTo(1);
    }

    // ==================== List ====================
    @Test
    void list_shouldReturnEnabledCategoriesSuccessfully()
            throws Exception {

        CategoryDTO category = new CategoryDTO();
        category.setId(1L);
        category.setName("Food");
        category.setType(1);
        category.setStatus(1);
        category.setSort(10);
        category.setDescription("Hot dishes");

        when(categoryService.list())
                .thenReturn(List.of(category));

        mockMvc.perform(
                        get("/category/list")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code")
                        .value(1))
                .andExpect(jsonPath("$.msg")
                        .value("success"))
                .andExpect(jsonPath("$.data[0].id")
                        .value(1))
                .andExpect(jsonPath("$.data[0].name")
                        .value("Food"))
                .andExpect(jsonPath("$.data[0].type")
                        .value(1))
                .andExpect(jsonPath("$.data[0].status")
                        .value(1))
                .andExpect(jsonPath("$.data[0].sort")
                        .value(10))
                .andExpect(jsonPath("$.data[0].description")
                        .value("Hot dishes"));

        verify(categoryService)
                .list();
    }

    // ==================== Update ====================
    @Test
    void updateCategory_shouldUpdateCategorySuccessfully()
            throws Exception {

        CategoryUpdateDTO request = new CategoryUpdateDTO();
        request.setId(1L);
        request.setName("Food");
        request.setType(1);
        request.setStatus(1);
        request.setSort(10);
        request.setDescription("Hot dishes");

        doNothing().when(categoryService)
                .update(any(CategoryUpdateDTO.class));

        mockMvc.perform(
                        patch("/category")
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

        ArgumentCaptor<CategoryUpdateDTO> captor =
                ArgumentCaptor.forClass(CategoryUpdateDTO.class);

        verify(categoryService).update(captor.capture());

        CategoryUpdateDTO captured = captor.getValue();

        assertThat(captured.getId())
                .isEqualTo(1L);
        assertThat(captured.getName())
                .isEqualTo("Food");
        assertThat(captured.getType())
                .isEqualTo(1);
        assertThat(captured.getStatus())
                .isEqualTo(1);
        assertThat(captured.getSort())
                .isEqualTo(10);
        assertThat(captured.getDescription())
                .isEqualTo("Hot dishes");
    }

    @Test
    void updateCategory_shouldRejectRequestWithoutId()
            throws Exception {

        CategoryUpdateDTO request = new CategoryUpdateDTO();
        request.setName("Food");

        mockMvc.perform(
                        patch("/category")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code")
                        .value(ErrorCode.FAILURE))
                .andExpect(jsonPath("$.msg")
                        .value("category id cannot be null"));

        verifyNoInteractions(categoryService);
    }

    @Test
    void updateCategory_shouldReturnNotFoundWhenCategoryDoesNotExist()
            throws Exception {

        CategoryUpdateDTO request = new CategoryUpdateDTO();
        request.setId(999L);
        request.setName("Food");

        doThrow(new BusinessException(
                ErrorCode.CATEGORY_NOT_FOUND,
                ErrorMessage.CATEGORY_NOT_FOUND
        )).when(categoryService)
                .update(any(CategoryUpdateDTO.class));

        mockMvc.perform(
                        patch("/category")
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

        verify(categoryService)
                .update(any(CategoryUpdateDTO.class));
    }

    // ==================== Delete ====================
    @Test
    void deleteCategory_shouldDeleteCategorySuccessfully()
            throws Exception {

        Long categoryId = 1L;

        doNothing().when(categoryService)
                .deleteById(categoryId);

        mockMvc.perform(
                        delete("/category/{id}", categoryId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code")
                        .value(1))
                .andExpect(jsonPath("$.msg")
                        .value("success"));

        verify(categoryService)
                .deleteById(categoryId);
    }

    @Test
    void deleteCategory_shouldRejectCategoryWithDishes()
            throws Exception {

        Long categoryId = 1L;

        doThrow(new BusinessException(
                ErrorCode.CATEGORY_HAS_DISHES,
                ErrorMessage.CATEGORY_HAS_DISHES
        )).when(categoryService)
                .deleteById(categoryId);

        mockMvc.perform(
                        delete("/category/{id}", categoryId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code")
                        .value(ErrorCode.CATEGORY_HAS_DISHES))
                .andExpect(jsonPath("$.msg")
                        .value(ErrorMessage.CATEGORY_HAS_DISHES));

        verify(categoryService)
                .deleteById(categoryId);
    }
}