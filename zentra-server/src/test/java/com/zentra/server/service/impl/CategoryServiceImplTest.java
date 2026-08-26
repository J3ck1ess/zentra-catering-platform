package com.zentra.server.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.zentra.common.constant.CategoryStatus;
import com.zentra.common.constant.CategoryType;
import com.zentra.common.constant.ErrorCode;
import com.zentra.common.constant.ErrorMessage;
import com.zentra.common.context.AuthContext;
import com.zentra.common.exception.BusinessException;
import com.zentra.common.result.PageResult;
import com.zentra.server.dto.CategoryCreateDTO;
import com.zentra.server.dto.CategoryDTO;
import com.zentra.server.dto.CategoryQueryDTO;
import com.zentra.server.dto.CategoryUpdateDTO;
import com.zentra.server.entity.Category;
import com.zentra.server.mapper.CategoryMapper;
import com.zentra.server.mapper.DishMapper;
import com.zentra.server.service.RedisService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.MockedStatic;
import org.springframework.dao.DuplicateKeyException;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CategoryServiceImplTest {

    private static final Long MERCHANT_ID = 100L;

    private CategoryMapper categoryMapper;
    private DishMapper dishMapper;
    private RedisService redisService;
    private CategoryServiceImpl categoryService;

    private MockedStatic<AuthContext> authContextMock;

    @BeforeEach
    void setUp() {
        categoryMapper = mock(CategoryMapper.class);
        dishMapper = mock(DishMapper.class);
        redisService = mock(RedisService.class);

        categoryService = new CategoryServiceImpl(
                categoryMapper,
                dishMapper,
                redisService
        );

        authContextMock = mockStatic(AuthContext.class);
        authContextMock.when(AuthContext::getCurrentMerchantId)
                .thenReturn(MERCHANT_ID);
    }

    @AfterEach
    void tearDown() {
        authContextMock.close();
    }

    // ==================== Create ====================
    @Test
    void create_shouldCreateCategorySuccessfully() {
        CategoryCreateDTO dto = new CategoryCreateDTO();
        dto.setName("Hot Dishes");
        dto.setType(CategoryType.DISH);
        dto.setSort(1);
        dto.setDescription("Main dish categories");

        when(categoryMapper.insert(any(Category.class)))
                .thenReturn(1);

        categoryService.create(dto);

        verify(categoryMapper).insert(argThat(category ->
                "Hot Dishes".equals(category.getName())
                        && category.getType() == CategoryType.DISH
                        && category.getStatus() == CategoryStatus.ENABLED
                        && MERCHANT_ID.equals(category.getMerchantId())
                        && Integer.valueOf(1).equals(category.getSort())
                        && "Main dish categories".equals(category.getDescription())
        ));

        verify(redisService).delete(anyString());
    }

    @Test
    void create_shouldRejectInvalidCategoryType() {
        CategoryCreateDTO dto = new CategoryCreateDTO();
        dto.setName("Invalid Category");
        dto.setType(999);

        assertThatThrownBy(() -> categoryService.create(dto))
                .satisfies(exception ->
                        assertBusinessException(
                                exception,
                                ErrorCode.CATEGORY_TYPE_INVALID,
                                ErrorMessage.CATEGORY_TYPE_INVALID
                        )
                );

        verify(categoryMapper, never()).insert(any(Category.class));
        verify(redisService, never()).delete(anyString());
    }

    @Test
    void create_shouldHandleDuplicateKeyException() {
        CategoryCreateDTO dto = new CategoryCreateDTO();
        dto.setName("Hot Dishes");
        dto.setType(CategoryType.DISH);

        when(categoryMapper.insert(any(Category.class)))
                .thenThrow(new DuplicateKeyException("Duplicate category"));

        assertThatThrownBy(() -> categoryService.create(dto))
                .satisfies(exception ->
                        assertBusinessException(
                                exception,
                                ErrorCode.CATEGORY_NAME_ALREADY_EXISTS,
                                ErrorMessage.CATEGORY_NAME_ALREADY_EXISTS
                        )
                );

        verify(categoryMapper).insert(any(Category.class));
        verify(redisService, never()).delete(anyString());
    }

    @Test
    void create_shouldRejectCreateFailure() {
        CategoryCreateDTO dto = new CategoryCreateDTO();
        dto.setName("Hot Dishes");
        dto.setType(CategoryType.DISH);

        when(categoryMapper.insert(any(Category.class)))
                .thenReturn(0);

        assertThatThrownBy(() -> categoryService.create(dto))
                .satisfies(exception ->
                        assertBusinessException(
                                exception,
                                ErrorCode.CATEGORY_CREATE_FAILED,
                                ErrorMessage.CATEGORY_CREATE_FAILED
                        )
                );

        verify(categoryMapper).insert(any(Category.class));
        verify(redisService, never()).delete(anyString());
    }

    // ==================== Page ====================
    @Test
    void page_shouldReturnPagedCategoriesSuccessfully() {
        CategoryQueryDTO queryDTO = new CategoryQueryDTO();
        queryDTO.setPage(1);
        queryDTO.setPageSize(10);

        Category category1 = new Category();
        category1.setId(1L);
        category1.setName("Hot Dishes");
        category1.setType(CategoryType.DISH);
        category1.setStatus(CategoryStatus.ENABLED);
        category1.setSort(1);
        category1.setMerchantId(MERCHANT_ID);

        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("Set Meals");
        category2.setType(CategoryType.SET_MEAL);
        category2.setStatus(CategoryStatus.ENABLED);
        category2.setSort(2);
        category2.setMerchantId(MERCHANT_ID);

        when(categoryMapper.findPage(
                null,
                null,
                null,
                MERCHANT_ID,
                0,
                10
        )).thenReturn(List.of(category1, category2));

        when(categoryMapper.count(
                null,
                null,
                null,
                MERCHANT_ID
        )).thenReturn(2L);

        PageResult<CategoryDTO> result = categoryService.page(queryDTO);

        assertThat(result.getTotal()).isEqualTo(2L);
        assertThat(result.getRecords()).hasSize(2);

        assertThat(result.getRecords().get(0).getId())
                .isEqualTo(1L);
        assertThat(result.getRecords().get(0).getName())
                .isEqualTo("Hot Dishes");

        assertThat(result.getRecords().get(1).getId())
                .isEqualTo(2L);
        assertThat(result.getRecords().get(1).getName())
                .isEqualTo("Set Meals");

        verify(categoryMapper).findPage(
                null,
                null,
                null,
                MERCHANT_ID,
                0,
                10
        );

        verify(categoryMapper).count(
                null,
                null,
                null,
                MERCHANT_ID
        );
    }

    @Test
    void page_shouldReturnFilteredCategoriesSuccessfully() {
        CategoryQueryDTO queryDTO = new CategoryQueryDTO();
        queryDTO.setPage(1);
        queryDTO.setPageSize(10);
        queryDTO.setName("Hot");
        queryDTO.setType(CategoryType.DISH);
        queryDTO.setStatus(CategoryStatus.ENABLED);

        Category category = new Category();
        category.setId(1L);
        category.setName("Hot Dishes");
        category.setType(CategoryType.DISH);
        category.setStatus(CategoryStatus.ENABLED);
        category.setSort(1);
        category.setMerchantId(MERCHANT_ID);

        when(categoryMapper.findPage(
                "Hot",
                CategoryType.DISH,
                CategoryStatus.ENABLED,
                MERCHANT_ID,
                0,
                10
        )).thenReturn(List.of(category));

        when(categoryMapper.count(
                "Hot",
                CategoryType.DISH,
                CategoryStatus.ENABLED,
                MERCHANT_ID
        )).thenReturn(1L);

        PageResult<CategoryDTO> result = categoryService.page(queryDTO);

        assertThat(result.getTotal()).isEqualTo(1L);
        assertThat(result.getRecords()).hasSize(1);

        assertThat(result.getRecords().get(0).getName())
                .isEqualTo("Hot Dishes");
        assertThat(result.getRecords().get(0).getType())
                .isEqualTo(CategoryType.DISH);
        assertThat(result.getRecords().get(0).getStatus())
                .isEqualTo(CategoryStatus.ENABLED);

        verify(categoryMapper).findPage(
                "Hot",
                CategoryType.DISH,
                CategoryStatus.ENABLED,
                MERCHANT_ID,
                0,
                10
        );

        verify(categoryMapper).count(
                "Hot",
                CategoryType.DISH,
                CategoryStatus.ENABLED,
                MERCHANT_ID
        );
    }

    @Test
    void page_shouldCalculateOffsetCorrectly() {
        CategoryQueryDTO queryDTO = new CategoryQueryDTO();
        queryDTO.setPage(2);
        queryDTO.setPageSize(10);

        Category category = new Category();
        category.setId(11L);
        category.setName("Desserts");
        category.setType(CategoryType.DISH);
        category.setStatus(CategoryStatus.ENABLED);
        category.setSort(11);
        category.setMerchantId(MERCHANT_ID);

        when(categoryMapper.findPage(
                null,
                null,
                null,
                MERCHANT_ID,
                10,
                10
        )).thenReturn(List.of(category));

        when(categoryMapper.count(
                null,
                null,
                null,
                MERCHANT_ID
        )).thenReturn(11L);

        PageResult<CategoryDTO> result = categoryService.page(queryDTO);

        assertThat(result.getTotal()).isEqualTo(11L);
        assertThat(result.getRecords()).hasSize(1);

        assertThat(result.getRecords().get(0).getId())
                .isEqualTo(11L);

        assertThat(result.getRecords().get(0).getName())
                .isEqualTo("Desserts");

        verify(categoryMapper).findPage(
                null,
                null,
                null,
                MERCHANT_ID,
                10,
                10
        );

        verify(categoryMapper).count(
                null,
                null,
                null,
                MERCHANT_ID
        );
    }

    // ==================== List ====================
    @Test
    void list_shouldReturnCachedCategories() {
        CategoryDTO category = new CategoryDTO();
        category.setId(1L);
        category.setName("Hot Dishes");
        category.setType(CategoryType.DISH);
        category.setStatus(CategoryStatus.ENABLED);

        List<CategoryDTO> cachedCategories = List.of(category);

        when(redisService.get(
                anyString(),
                ArgumentMatchers.<TypeReference<List<CategoryDTO>>>any()
        )).thenReturn(cachedCategories);

        List<CategoryDTO> result = categoryService.list();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getName()).isEqualTo("Hot Dishes");
        assertThat(result.get(0).getType()).isEqualTo(CategoryType.DISH);
        assertThat(result.get(0).getStatus()).isEqualTo(CategoryStatus.ENABLED);

        verify(redisService).get(
                anyString(),
                any(TypeReference.class)
        );

        verify(categoryMapper, never())
                .findEnabledCategories(anyLong(), anyInt());

        verify(redisService, never()).set(
                anyString(),
                any(),
                any(Duration.class)
        );
    }

    @Test
    void list_shouldQueryDatabaseAndCacheResultWhenCacheMiss() {
        Category category1 = new Category();
        category1.setId(1L);
        category1.setName("Hot Dishes");
        category1.setType(CategoryType.DISH);
        category1.setStatus(CategoryStatus.ENABLED);
        category1.setSort(1);
        category1.setMerchantId(MERCHANT_ID);

        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("Set Meals");
        category2.setType(CategoryType.SET_MEAL);
        category2.setStatus(CategoryStatus.ENABLED);
        category2.setSort(2);
        category2.setMerchantId(MERCHANT_ID);

        when(redisService.get(
                anyString(),
                ArgumentMatchers.<TypeReference<List<CategoryDTO>>>any()
        )).thenReturn(null);

        when(categoryMapper.findEnabledCategories(
                MERCHANT_ID,
                CategoryStatus.ENABLED
        )).thenReturn(List.of(category1, category2));

        List<CategoryDTO> result = categoryService.list();

        assertThat(result).hasSize(2);

        assertThat(result.get(0).getId())
                .isEqualTo(1L);
        assertThat(result.get(0).getName())
                .isEqualTo("Hot Dishes");

        assertThat(result.get(1).getId())
                .isEqualTo(2L);
        assertThat(result.get(1).getName())
                .isEqualTo("Set Meals");

        verify(redisService).get(
                anyString(),
                ArgumentMatchers.<TypeReference<List<CategoryDTO>>>any()
        );

        verify(categoryMapper).findEnabledCategories(
                MERCHANT_ID,
                CategoryStatus.ENABLED
        );

        verify(redisService).set(
                anyString(),
                eq(result),
                any(Duration.class)
        );
    }

    @Test
    void list_shouldReturnEmptyListWhenCacheContainsEmptyList() {
        List<CategoryDTO> cachedCategories = List.of();

        when(redisService.get(
                anyString(),
                ArgumentMatchers.<TypeReference<List<CategoryDTO>>>any()
        )).thenReturn(cachedCategories);

        List<CategoryDTO> result = categoryService.list();

        assertThat(result).isEmpty();

        verify(redisService).get(
                anyString(),
                ArgumentMatchers.<TypeReference<List<CategoryDTO>>>any()
        );

        verify(categoryMapper, never()).findEnabledCategories(
                anyLong(),
                anyInt()
        );

        verify(redisService, never()).set(
                anyString(),
                any(),
                any(Duration.class)
        );
    }

    // ==================== Update ====================
    @Test
    void update_shouldUpdateCategorySuccessfully() {
        CategoryUpdateDTO dto = new CategoryUpdateDTO();
        dto.setId(1L);
        dto.setName("Updated Dishes");
        dto.setType(CategoryType.DISH);
        dto.setSort(10);

        Category existingCategory = new Category();
        existingCategory.setId(1L);
        existingCategory.setName("Old Dishes");
        existingCategory.setType(CategoryType.DISH);
        existingCategory.setStatus(CategoryStatus.ENABLED);
        existingCategory.setMerchantId(MERCHANT_ID);

        when(categoryMapper.findById(1L, MERCHANT_ID))
                .thenReturn(existingCategory);

        when(categoryMapper.update(any(Category.class)))
                .thenReturn(1);

        categoryService.update(dto);

        verify(categoryMapper).findById(1L, MERCHANT_ID);

        verify(categoryMapper).update(argThat(category ->
                Long.valueOf(1L).equals(category.getId())
                        && "Updated Dishes".equals(category.getName())
                        && category.getType() == CategoryType.DISH
                        && Integer.valueOf(10).equals(category.getSort())
                        && MERCHANT_ID.equals(category.getMerchantId())
        ));
    }

    @Test
    void update_shouldRejectWhenCategoryNotFound() {
        CategoryUpdateDTO dto = new CategoryUpdateDTO();
        dto.setId(999L);
        dto.setName("Updated Dishes");
        dto.setType(CategoryType.DISH);
        dto.setSort(10);

        when(categoryMapper.findById(999L, MERCHANT_ID))
                .thenReturn(null);

        assertThatThrownBy(() -> categoryService.update(dto))
                .satisfies(exception ->
                        assertBusinessException(
                                exception,
                                ErrorCode.CATEGORY_NOT_FOUND,
                                ErrorMessage.CATEGORY_NOT_FOUND
                        )
                );

        verify(categoryMapper).findById(999L, MERCHANT_ID);

        verify(categoryMapper, never())
                .update(any(Category.class));
    }

    @Test
    void update_shouldRejectEmptyUpdate() {
        CategoryUpdateDTO dto = new CategoryUpdateDTO();
        dto.setId(1L);

        assertThatThrownBy(() -> categoryService.update(dto))
                .satisfies(exception ->
                        assertBusinessException(
                                exception,
                                ErrorCode.CATEGORY_UPDATE_FAILED,
                                ErrorMessage.CATEGORY_UPDATE_FAILED
                        )
                );

        verify(categoryMapper, never())
                .findById(anyLong(), anyLong());

        verify(categoryMapper, never())
                .update(any(Category.class));

        verify(redisService, never())
                .delete(anyString());
    }

    @Test
    void update_shouldRejectInvalidCategoryType() {
        CategoryUpdateDTO dto = new CategoryUpdateDTO();
        dto.setId(1L);
        dto.setType(999);

        assertThatThrownBy(() -> categoryService.update(dto))
                .satisfies(exception ->
                        assertBusinessException(
                                exception,
                                ErrorCode.CATEGORY_TYPE_INVALID,
                                ErrorMessage.CATEGORY_TYPE_INVALID
                        )
                );

        verify(categoryMapper, never())
                .findById(anyLong(), anyLong());

        verify(categoryMapper, never())
                .update(any(Category.class));

        verify(redisService, never())
                .delete(anyString());
    }

    @Test
    void update_shouldRejectInvalidCategoryStatus() {
        CategoryUpdateDTO dto = new CategoryUpdateDTO();
        dto.setId(1L);
        dto.setStatus(999);

        assertThatThrownBy(() -> categoryService.update(dto))
                .satisfies(exception ->
                        assertBusinessException(
                                exception,
                                ErrorCode.CATEGORY_STATUS_INVALID,
                                ErrorMessage.CATEGORY_STATUS_INVALID
                        )
                );

        verify(categoryMapper, never())
                .findById(anyLong(), anyLong());

        verify(categoryMapper, never())
                .update(any(Category.class));

        verify(redisService, never())
                .delete(anyString());
    }

    @Test
    void update_shouldRejectUpdateFailure() {
        CategoryUpdateDTO dto = new CategoryUpdateDTO();
        dto.setId(1L);
        dto.setName("Updated Dishes");
        dto.setType(CategoryType.DISH);
        dto.setSort(10);

        Category existingCategory = new Category();
        existingCategory.setId(1L);
        existingCategory.setName("Old Dishes");
        existingCategory.setType(CategoryType.DISH);
        existingCategory.setStatus(CategoryStatus.ENABLED);
        existingCategory.setMerchantId(MERCHANT_ID);

        when(categoryMapper.findById(1L, MERCHANT_ID))
                .thenReturn(existingCategory);

        when(categoryMapper.update(any(Category.class)))
                .thenReturn(0);

        assertThatThrownBy(() -> categoryService.update(dto))
                .satisfies(exception ->
                        assertBusinessException(
                                exception,
                                ErrorCode.CATEGORY_UPDATE_FAILED,
                                ErrorMessage.CATEGORY_UPDATE_FAILED
                        )
                );

        verify(categoryMapper).findById(1L, MERCHANT_ID);

        verify(categoryMapper).update(any(Category.class));

        verify(redisService, never()).delete(anyString());
    }

    @Test
    void update_shouldHandleDuplicateKeyException() {
        CategoryUpdateDTO dto = new CategoryUpdateDTO();
        dto.setId(1L);
        dto.setName("Hot Dishes");
        dto.setType(CategoryType.DISH);
        dto.setSort(10);

        Category existingCategory = new Category();
        existingCategory.setId(1L);
        existingCategory.setName("Old Dishes");
        existingCategory.setType(CategoryType.DISH);
        existingCategory.setStatus(CategoryStatus.ENABLED);
        existingCategory.setMerchantId(MERCHANT_ID);

        when(categoryMapper.findById(1L, MERCHANT_ID))
                .thenReturn(existingCategory);

        when(categoryMapper.update(any(Category.class)))
                .thenThrow(new DuplicateKeyException("Duplicate category"));

        assertThatThrownBy(() -> categoryService.update(dto))
                .satisfies(exception ->
                        assertBusinessException(
                                exception,
                                ErrorCode.CATEGORY_NAME_ALREADY_EXISTS,
                                ErrorMessage.CATEGORY_NAME_ALREADY_EXISTS
                        )
                );

        verify(categoryMapper).findById(1L, MERCHANT_ID);

        verify(categoryMapper).update(any(Category.class));

        verify(redisService, never())
                .delete(anyString());
    }

    @Test
    void update_shouldDeleteCacheAfterSuccessfulUpdate() {
        CategoryUpdateDTO dto = new CategoryUpdateDTO();
        dto.setId(1L);
        dto.setName("Updated Dishes");
        dto.setType(CategoryType.DISH);
        dto.setSort(10);

        Category existingCategory = new Category();
        existingCategory.setId(1L);
        existingCategory.setName("Old Dishes");
        existingCategory.setType(CategoryType.DISH);
        existingCategory.setStatus(CategoryStatus.ENABLED);
        existingCategory.setMerchantId(MERCHANT_ID);

        when(categoryMapper.findById(1L, MERCHANT_ID))
                .thenReturn(existingCategory);

        when(categoryMapper.update(any(Category.class)))
                .thenReturn(1);

        categoryService.update(dto);

        verify(categoryMapper).findById(1L, MERCHANT_ID);
        verify(categoryMapper).update(any(Category.class));

        verify(redisService).delete(anyString());
    }

    // ==================== Delete ====================
    @Test
    void deleteById_shouldDeleteCategorySuccessfully() {
        Long categoryId = 1L;

        Category existingCategory = new Category();
        existingCategory.setId(categoryId);
        existingCategory.setName("Hot Dishes");
        existingCategory.setType(CategoryType.DISH);
        existingCategory.setStatus(CategoryStatus.ENABLED);
        existingCategory.setMerchantId(MERCHANT_ID);

        when(categoryMapper.findById(categoryId, MERCHANT_ID))
                .thenReturn(existingCategory);

        when(categoryMapper.deleteById(categoryId, MERCHANT_ID))
                .thenReturn(1);

        categoryService.deleteById(categoryId);

        verify(categoryMapper).findById(categoryId, MERCHANT_ID);

        verify(categoryMapper).deleteById(
                categoryId,
                MERCHANT_ID
        );

        verify(redisService).delete(anyString());
    }

    @Test
    void deleteById_shouldRejectWhenCategoryNotFound() {
        Long categoryId = 999L;

        when(categoryMapper.findById(categoryId, MERCHANT_ID))
                .thenReturn(null);

        assertThatThrownBy(() -> categoryService.deleteById(categoryId))
                .satisfies(exception ->
                        assertBusinessException(
                                exception,
                                ErrorCode.CATEGORY_NOT_FOUND,
                                ErrorMessage.CATEGORY_NOT_FOUND
                        )
                );

        verify(categoryMapper).findById(
                categoryId,
                MERCHANT_ID
        );

        verify(categoryMapper, never())
                .deleteById(
                        anyLong(),
                        anyLong()
                );

        verify(redisService, never())
                .delete(anyString());
    }

    @Test
    void deleteById_shouldRejectDeleteFailure() {
        Long categoryId = 1L;

        Category existingCategory = new Category();
        existingCategory.setId(categoryId);
        existingCategory.setName("Hot Dishes");
        existingCategory.setType(CategoryType.DISH);
        existingCategory.setStatus(CategoryStatus.ENABLED);
        existingCategory.setMerchantId(MERCHANT_ID);

        when(categoryMapper.findById(categoryId, MERCHANT_ID))
                .thenReturn(existingCategory);

        when(categoryMapper.deleteById(categoryId, MERCHANT_ID))
                .thenReturn(0);

        assertThatThrownBy(() -> categoryService.deleteById(categoryId))
                .satisfies(exception ->
                        assertBusinessException(
                                exception,
                                ErrorCode.CATEGORY_DELETE_FAILED,
                                ErrorMessage.CATEGORY_DELETE_FAILED
                        )
                );

        verify(categoryMapper).findById(
                categoryId,
                MERCHANT_ID
        );

        verify(categoryMapper).deleteById(
                categoryId,
                MERCHANT_ID
        );

        verify(redisService, never())
                .delete(anyString());
    }

    @Test
    void deleteById_shouldRejectWhenCategoryHasDishes() {
        Long categoryId = 1L;

        Category existingCategory = new Category();
        existingCategory.setId(categoryId);
        existingCategory.setName("Hot Dishes");
        existingCategory.setType(CategoryType.DISH);
        existingCategory.setStatus(CategoryStatus.ENABLED);
        existingCategory.setMerchantId(MERCHANT_ID);

        when(categoryMapper.findById(categoryId, MERCHANT_ID))
                .thenReturn(existingCategory);

        when(dishMapper.countByCategoryId(categoryId, MERCHANT_ID))
                .thenReturn(1);

        assertThatThrownBy(() -> categoryService.deleteById(categoryId))
                .satisfies(exception ->
                        assertBusinessException(
                                exception,
                                ErrorCode.CATEGORY_HAS_DISHES,
                                ErrorMessage.CATEGORY_HAS_DISHES
                        )
                );

        verify(categoryMapper).findById(
                categoryId,
                MERCHANT_ID
        );

        verify(dishMapper).countByCategoryId(
                categoryId,
                MERCHANT_ID
        );

        verify(categoryMapper, never())
                .deleteById(
                        anyLong(),
                        anyLong()
                );

        verify(redisService, never())
                .delete(anyString());
    }

    private void assertBusinessException(
            Throwable exception,
            Integer expectedCode,
            String expectedMessage
    ) {
        assertThat(exception)
                .isInstanceOf(BusinessException.class);

        BusinessException businessException =
                (BusinessException) exception;

        assertThat(businessException.getCode())
                .isEqualTo(expectedCode);

        assertThat(businessException.getMessage())
                .isEqualTo(expectedMessage);
    }
}