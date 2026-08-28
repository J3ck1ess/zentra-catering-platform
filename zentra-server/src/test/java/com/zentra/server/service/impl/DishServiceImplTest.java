package com.zentra.server.service.impl;

import com.zentra.common.constant.*;
import com.zentra.common.context.AuthContext;
import com.zentra.common.exception.BusinessException;
import com.zentra.common.result.PageResult;
import com.zentra.server.dto.DishCreateDTO;
import com.zentra.server.dto.DishDTO;
import com.zentra.server.dto.DishQueryDTO;
import com.zentra.server.dto.DishUpdateDTO;
import com.zentra.server.entity.Category;
import com.zentra.server.entity.Dish;
import com.zentra.server.mapper.CategoryMapper;
import com.zentra.server.mapper.DishMapper;
import com.zentra.server.service.RedisService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DishServiceImplTest {

    private static final Long MERCHANT_ID = 100L;

    @Mock
    private DishMapper dishMapper;

    @Mock
    private CategoryMapper categoryMapper;

    @Mock
    private RedisService redisService;

    @InjectMocks
    private DishServiceImpl dishService;

    private MockedStatic<AuthContext> authContextMock;

    @BeforeEach
    void setUp() {
        authContextMock = org.mockito.Mockito.mockStatic(AuthContext.class);
        authContextMock.when(AuthContext::getCurrentMerchantId)
                .thenReturn(MERCHANT_ID);
    }

    @AfterEach
    void tearDown() {
        authContextMock.close();
    }

    // ==================== Create ====================
    @Test
    void create_shouldCreateDishSuccessfully() {
        DishCreateDTO dto = new DishCreateDTO();
        dto.setName("Kung Pao Chicken");
        dto.setCategoryId(1L);
        dto.setPrice(new BigDecimal("28.00"));

        Category category = new Category();
        category.setId(1L);
        category.setName("Main Dishes");
        category.setType(CategoryType.DISH);
        category.setStatus(CategoryStatus.ENABLED);
        category.setMerchantId(MERCHANT_ID);

        when(categoryMapper.findById(1L, MERCHANT_ID))
                .thenReturn(category);

        when(dishMapper.insert(any(Dish.class)))
                .thenReturn(1);

        dishService.create(dto);

        verify(categoryMapper)
                .findById(1L, MERCHANT_ID);

        verify(dishMapper)
                .insert(argThat(dish ->
                        "Kung Pao Chicken".equals(dish.getName())
                                && Long.valueOf(1L).equals(dish.getCategoryId())
                                && new BigDecimal("28.00").equals(dish.getPrice())
                                && dish.getStatus() == DishStatus.ENABLED
                                && MERCHANT_ID.equals(dish.getMerchantId())
                ));
    }

    @Test
    void create_shouldRejectWhenCategoryNotFound() {
        DishCreateDTO dto = new DishCreateDTO();
        dto.setName("Kung Pao Chicken");
        dto.setCategoryId(999L);
        dto.setPrice(new BigDecimal("28.00"));

        when(categoryMapper.findById(999L, MERCHANT_ID))
                .thenReturn(null);

        assertThatThrownBy(() -> dishService.create(dto))
                .satisfies(exception ->
                        assertBusinessException(
                                exception,
                                ErrorCode.CATEGORY_NOT_FOUND,
                                ErrorMessage.CATEGORY_NOT_FOUND
                        )
                );

        verify(categoryMapper)
                .findById(999L, MERCHANT_ID);

        verify(dishMapper, never())
                .insert(any(Dish.class));
    }

    @Test
    void create_shouldRejectCreateFailure() {
        DishCreateDTO dto = new DishCreateDTO();
        dto.setName("Kung Pao Chicken");
        dto.setCategoryId(1L);
        dto.setPrice(new BigDecimal("28.00"));

        Category category = new Category();
        category.setId(1L);
        category.setName("Main Dishes");
        category.setType(CategoryType.DISH);
        category.setStatus(CategoryStatus.ENABLED);
        category.setMerchantId(MERCHANT_ID);

        when(categoryMapper.findById(1L, MERCHANT_ID))
                .thenReturn(category);

        when(dishMapper.insert(any(Dish.class)))
                .thenReturn(0);

        assertThatThrownBy(() -> dishService.create(dto))
                .satisfies(exception ->
                        assertBusinessException(
                                exception,
                                ErrorCode.DISH_CREATE_FAILED,
                                ErrorMessage.DISH_CREATE_FAILED
                        )
                );

        verify(categoryMapper)
                .findById(1L, MERCHANT_ID);

        verify(dishMapper)
                .insert(any(Dish.class));
    }

    @Test
    void create_shouldHandleDuplicateKeyException() {
        DishCreateDTO dto = new DishCreateDTO();
        dto.setName("Kung Pao Chicken");
        dto.setCategoryId(1L);
        dto.setPrice(new BigDecimal("28.00"));

        Category category = new Category();
        category.setId(1L);
        category.setName("Main Dishes");
        category.setType(CategoryType.DISH);
        category.setStatus(CategoryStatus.ENABLED);
        category.setMerchantId(MERCHANT_ID);

        when(categoryMapper.findById(1L, MERCHANT_ID))
                .thenReturn(category);

        when(dishMapper.insert(any(Dish.class)))
                .thenThrow(new DuplicateKeyException("Duplicate dish name"));

        assertThatThrownBy(() -> dishService.create(dto))
                .satisfies(exception ->
                        assertBusinessException(
                                exception,
                                ErrorCode.DISH_NAME_ALREADY_EXISTS,
                                ErrorMessage.DISH_NAME_ALREADY_EXISTS
                        )
                );

        verify(categoryMapper)
                .findById(1L, MERCHANT_ID);

        verify(dishMapper)
                .insert(any(Dish.class));
    }

    // ==================== Page ====================
    @Test
    void page_shouldReturnPagedDishes() {
        DishQueryDTO query = new DishQueryDTO();
        query.setPage(1);
        query.setPageSize(10);

        Dish dish = new Dish();
        dish.setId(1L);
        dish.setName("Kung Pao Chicken");
        dish.setPrice(new BigDecimal("28.00"));
        dish.setCategoryId(1L);
        dish.setCategoryName("Main Dishes");
        dish.setStatus(DishStatus.ENABLED);
        dish.setMerchantId(MERCHANT_ID);

        when(dishMapper.findPage(
                null,
                null,
                null,
                MERCHANT_ID,
                0,
                10
        )).thenReturn(List.of(dish));

        when(dishMapper.count(
                null,
                null,
                null,
                MERCHANT_ID
        )).thenReturn(1L);

        PageResult<DishDTO> result = dishService.page(query);

        assertThat(result).isNotNull();
        assertThat(result.getTotal()).isEqualTo(1L);
        assertThat(result.getRecords()).hasSize(1);

        DishDTO record = result.getRecords().get(0);

        assertThat(record.getId()).isEqualTo(1L);
        assertThat(record.getName()).isEqualTo("Kung Pao Chicken");
        assertThat(record.getPrice()).isEqualTo(new BigDecimal("28.00"));
        assertThat(record.getCategoryId()).isEqualTo(1L);
        assertThat(record.getCategoryName()).isEqualTo("Main Dishes");
        assertThat(record.getStatus()).isEqualTo(DishStatus.ENABLED);

        verify(dishMapper).findPage(
                null,
                null,
                null,
                MERCHANT_ID,
                0,
                10
        );

        verify(dishMapper).count(
                null,
                null,
                null,
                MERCHANT_ID
        );
    }

    @Test
    void page_shouldApplyQueryFilters() {
        DishQueryDTO query = new DishQueryDTO();
        query.setPage(2);
        query.setPageSize(5);
        query.setName("Chicken");
        query.setCategoryId(1L);
        query.setStatus(DishStatus.ENABLED);

        Dish dish = new Dish();
        dish.setId(2L);
        dish.setName("Chicken Burger");
        dish.setPrice(new BigDecimal("25.00"));
        dish.setCategoryId(1L);
        dish.setCategoryName("Main Dishes");
        dish.setStatus(DishStatus.ENABLED);
        dish.setMerchantId(MERCHANT_ID);

        when(dishMapper.findPage(
                "Chicken",
                1L,
                DishStatus.ENABLED,
                MERCHANT_ID,
                5,
                5
        )).thenReturn(List.of(dish));

        when(dishMapper.count(
                "Chicken",
                1L,
                DishStatus.ENABLED,
                MERCHANT_ID
        )).thenReturn(1L);

        PageResult<DishDTO> result = dishService.page(query);

        assertThat(result).isNotNull();
        assertThat(result.getTotal()).isEqualTo(1L);
        assertThat(result.getRecords()).hasSize(1);

        DishDTO record = result.getRecords().get(0);

        assertThat(record.getName()).isEqualTo("Chicken Burger");
        assertThat(record.getCategoryId()).isEqualTo(1L);
        assertThat(record.getStatus()).isEqualTo(DishStatus.ENABLED);

        verify(dishMapper).findPage(
                "Chicken",
                1L,
                DishStatus.ENABLED,
                MERCHANT_ID,
                5,
                5
        );

        verify(dishMapper).count(
                "Chicken",
                1L,
                DishStatus.ENABLED,
                MERCHANT_ID
        );
    }

    // ==================== List ====================
    @Test
    void list_shouldReturnEnabledDishes() {
        Long categoryId = 1L;

        Dish dish1 = new Dish();
        dish1.setId(1L);
        dish1.setName("Kung Pao Chicken");
        dish1.setPrice(new BigDecimal("28.00"));
        dish1.setCategoryId(categoryId);
        dish1.setCategoryName("Main Dishes");
        dish1.setStatus(DishStatus.ENABLED);
        dish1.setMerchantId(MERCHANT_ID);

        Dish dish2 = new Dish();
        dish2.setId(2L);
        dish2.setName("Sweet and Sour Chicken");
        dish2.setPrice(new BigDecimal("26.00"));
        dish2.setCategoryId(categoryId);
        dish2.setCategoryName("Main Dishes");
        dish2.setStatus(DishStatus.ENABLED);
        dish2.setMerchantId(MERCHANT_ID);

        when(dishMapper.findEnabledDishes(
                categoryId,
                MERCHANT_ID,
                DishStatus.ENABLED
        )).thenReturn(List.of(dish1, dish2));

        List<DishDTO> result = dishService.list(categoryId);

        assertThat(result).hasSize(2);

        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getName()).isEqualTo("Kung Pao Chicken");
        assertThat(result.get(0).getPrice())
                .isEqualTo(new BigDecimal("28.00"));
        assertThat(result.get(0).getCategoryId())
                .isEqualTo(categoryId);
        assertThat(result.get(0).getStatus())
                .isEqualTo(DishStatus.ENABLED);

        assertThat(result.get(1).getId()).isEqualTo(2L);
        assertThat(result.get(1).getName())
                .isEqualTo("Sweet and Sour Chicken");
        assertThat(result.get(1).getPrice())
                .isEqualTo(new BigDecimal("26.00"));
        assertThat(result.get(1).getCategoryId())
                .isEqualTo(categoryId);
        assertThat(result.get(1).getStatus())
                .isEqualTo(DishStatus.ENABLED);

        verify(dishMapper).findEnabledDishes(
                categoryId,
                MERCHANT_ID,
                DishStatus.ENABLED
        );
    }

    @Test
    void list_shouldRejectWhenCategoryIdIsNull() {
        assertThatThrownBy(() -> dishService.list(null))
                .satisfies(exception ->
                        assertBusinessException(
                                exception,
                                ErrorCode.CATEGORY_ID_REQUIRED,
                                ErrorMessage.CATEGORY_ID_REQUIRED
                        )
                );

        verify(dishMapper, never())
                .findEnabledDishes(
                        anyLong(),
                        anyLong(),
                        anyInt()
                );
    }

    // ==================== Get By ID ====================
    @Test
    void getById_shouldReturnCachedDish() {
        Long dishId = 1L;

        DishDTO cachedDish = new DishDTO();
        cachedDish.setId(dishId);
        cachedDish.setName("Kung Pao Chicken");
        cachedDish.setPrice(new BigDecimal("28.00"));
        cachedDish.setCategoryId(1L);
        cachedDish.setStatus(DishStatus.ENABLED);

        when(redisService.get(
                anyString(),
                eq(DishDTO.class)
        )).thenReturn(cachedDish);

        DishDTO result = dishService.getById(dishId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(dishId);
        assertThat(result.getName())
                .isEqualTo("Kung Pao Chicken");
        assertThat(result.getPrice())
                .isEqualTo(new BigDecimal("28.00"));
        assertThat(result.getCategoryId())
                .isEqualTo(1L);
        assertThat(result.getStatus())
                .isEqualTo(DishStatus.ENABLED);

        verify(redisService)
                .get(
                        anyString(),
                        eq(DishDTO.class)
                );

        verify(dishMapper, never())
                .findById(anyLong(), anyLong());

        verify(redisService, never())
                .set(
                        anyString(),
                        any(),
                        any(Duration.class)
                );
    }

    @Test
    void getById_shouldQueryDatabaseAndCacheWhenCacheMiss() {
        Long dishId = 1L;

        Dish dish = new Dish();
        dish.setId(dishId);
        dish.setName("Kung Pao Chicken");
        dish.setPrice(new BigDecimal("28.00"));
        dish.setCategoryId(1L);
        dish.setCategoryName("Main Dishes");
        dish.setStatus(DishStatus.ENABLED);
        dish.setMerchantId(MERCHANT_ID);

        when(redisService.get(
                anyString(),
                eq(DishDTO.class)
        )).thenReturn(null);

        when(dishMapper.findById(
                dishId,
                MERCHANT_ID
        )).thenReturn(dish);

        DishDTO result = dishService.getById(dishId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(dishId);
        assertThat(result.getName())
                .isEqualTo("Kung Pao Chicken");
        assertThat(result.getPrice())
                .isEqualTo(new BigDecimal("28.00"));
        assertThat(result.getCategoryId())
                .isEqualTo(1L);
        assertThat(result.getStatus())
                .isEqualTo(DishStatus.ENABLED);

        verify(redisService)
                .get(
                        anyString(),
                        eq(DishDTO.class)
                );

        verify(dishMapper)
                .findById(
                        dishId,
                        MERCHANT_ID
                );

        verify(redisService)
                .set(
                        anyString(),
                        any(DishDTO.class),
                        any(Duration.class)
                );
    }

    @Test
    void getById_shouldRejectWhenDishNotFound() {
        Long dishId = 999L;

        when(redisService.get(
                anyString(),
                eq(DishDTO.class)
        )).thenReturn(null);

        when(dishMapper.findById(
                dishId,
                MERCHANT_ID
        )).thenReturn(null);

        assertThatThrownBy(() -> dishService.getById(dishId))
                .satisfies(exception ->
                        assertBusinessException(
                                exception,
                                ErrorCode.DISH_NOT_FOUND,
                                ErrorMessage.DISH_NOT_FOUND
                        )
                );

        verify(redisService)
                .get(
                        anyString(),
                        eq(DishDTO.class)
                );

        verify(dishMapper)
                .findById(
                        dishId,
                        MERCHANT_ID
                );

        verify(redisService, never())
                .set(
                        anyString(),
                        any(),
                        any(Duration.class)
                );
    }

    // ==================== Update ====================
    @Test
    void update_shouldUpdateDishSuccessfully() {
        DishUpdateDTO dto = new DishUpdateDTO();
        dto.setId(1L);
        dto.setName("Updated Kung Pao Chicken");
        dto.setCategoryId(2L);
        dto.setPrice(new BigDecimal("30.00"));
        dto.setStatus(DishStatus.ENABLED);

        Dish dbDish = new Dish();
        dbDish.setId(1L);
        dbDish.setName("Kung Pao Chicken");
        dbDish.setPrice(new BigDecimal("28.00"));
        dbDish.setCategoryId(1L);
        dbDish.setStatus(DishStatus.ENABLED);
        dbDish.setMerchantId(MERCHANT_ID);

        Category category = new Category();
        category.setId(2L);
        category.setName("Main Dishes");
        category.setType(CategoryType.DISH);
        category.setStatus(CategoryStatus.ENABLED);
        category.setMerchantId(MERCHANT_ID);

        when(dishMapper.findById(
                dto.getId(),
                MERCHANT_ID
        )).thenReturn(dbDish);

        when(categoryMapper.findById(
                dto.getCategoryId(),
                MERCHANT_ID
        )).thenReturn(category);

        when(dishMapper.update(any(Dish.class)))
                .thenReturn(1);

        dishService.update(dto);

        verify(dishMapper)
                .findById(
                        dto.getId(),
                        MERCHANT_ID
                );

        verify(categoryMapper)
                .findById(
                        dto.getCategoryId(),
                        MERCHANT_ID
                );

        verify(dishMapper)
                .update(argThat(dish ->
                        dto.getId().equals(dish.getId())
                                && dto.getName().equals(dish.getName())
                                && dto.getCategoryId().equals(dish.getCategoryId())
                                && dto.getPrice().equals(dish.getPrice())
                                && dto.getStatus().equals(dish.getStatus())
                                && MERCHANT_ID.equals(dish.getMerchantId())
                ));

        verify(redisService)
                .delete(anyString());
    }

    @Test
    void update_shouldRejectWhenCategoryNotFound() {
        DishUpdateDTO dto = new DishUpdateDTO();
        dto.setId(1L);
        dto.setName("Updated Kung Pao Chicken");
        dto.setCategoryId(999L);
        dto.setPrice(new BigDecimal("30.00"));
        dto.setStatus(DishStatus.ENABLED);

        Dish existingDish = new Dish();
        existingDish.setId(1L);
        existingDish.setName("Kung Pao Chicken");
        existingDish.setPrice(new BigDecimal("28.00"));
        existingDish.setCategoryId(1L);
        existingDish.setStatus(DishStatus.ENABLED);
        existingDish.setMerchantId(MERCHANT_ID);

        when(dishMapper.findById(
                dto.getId(),
                MERCHANT_ID
        )).thenReturn(existingDish);

        when(categoryMapper.findById(
                dto.getCategoryId(),
                MERCHANT_ID
        )).thenReturn(null);

        assertThatThrownBy(() -> dishService.update(dto))
                .satisfies(exception ->
                        assertBusinessException(
                                exception,
                                ErrorCode.CATEGORY_NOT_FOUND,
                                ErrorMessage.CATEGORY_NOT_FOUND
                        )
                );

        verify(dishMapper)
                .findById(
                        dto.getId(),
                        MERCHANT_ID
                );

        verify(categoryMapper)
                .findById(
                        dto.getCategoryId(),
                        MERCHANT_ID
                );

        verify(dishMapper, never())
                .update(any(Dish.class));

        verify(redisService, never())
                .delete(anyString());
    }

    @Test
    void update_shouldRejectWhenDishNotFound() {
        DishUpdateDTO dto = new DishUpdateDTO();
        dto.setId(999L);
        dto.setName("Updated Kung Pao Chicken");
        dto.setCategoryId(2L);
        dto.setPrice(new BigDecimal("30.00"));
        dto.setStatus(DishStatus.ENABLED);

        when(dishMapper.findById(
                dto.getId(),
                MERCHANT_ID
        )).thenReturn(null);

        assertThatThrownBy(() -> dishService.update(dto))
                .satisfies(exception ->
                        assertBusinessException(
                                exception,
                                ErrorCode.DISH_NOT_FOUND,
                                ErrorMessage.DISH_NOT_FOUND
                        )
                );

        verify(dishMapper)
                .findById(
                        dto.getId(),
                        MERCHANT_ID
                );

        verify(categoryMapper, never())
                .findById(
                        anyLong(),
                        anyLong()
                );

        verify(dishMapper, never())
                .update(any(Dish.class));

        verify(redisService, never())
                .delete(anyString());
    }

    @Test
    void update_shouldRejectWhenNoFieldsProvided() {
        DishUpdateDTO dto = new DishUpdateDTO();
        dto.setId(1L);

        assertThatThrownBy(() -> dishService.update(dto))
                .satisfies(exception ->
                        assertBusinessException(
                                exception,
                                ErrorCode.DISH_UPDATE_FAILED,
                                ErrorMessage.DISH_UPDATE_FAILED
                        )
                );

        verify(dishMapper, never())
                .findById(
                        anyLong(),
                        anyLong()
                );

        verify(categoryMapper, never())
                .findById(
                        anyLong(),
                        anyLong()
                );

        verify(dishMapper, never())
                .update(any(Dish.class));

        verify(redisService, never())
                .delete(anyString());
    }

    @Test
    void update_shouldRejectWhenDishStatusIsInvalid() {
        DishUpdateDTO dto = new DishUpdateDTO();
        dto.setId(1L);
        dto.setName("Updated Kung Pao Chicken");
        dto.setCategoryId(2L);
        dto.setPrice(new BigDecimal("30.00"));
        dto.setStatus(99);

        Dish existingDish = new Dish();
        existingDish.setId(1L);
        existingDish.setName("Kung Pao Chicken");
        existingDish.setPrice(new BigDecimal("28.00"));
        existingDish.setCategoryId(1L);
        existingDish.setStatus(DishStatus.ENABLED);
        existingDish.setMerchantId(MERCHANT_ID);

        Category category = new Category();
        category.setId(2L);
        category.setName("Main Dishes");
        category.setType(CategoryType.DISH);
        category.setStatus(CategoryStatus.ENABLED);
        category.setMerchantId(MERCHANT_ID);

        when(dishMapper.findById(
                dto.getId(),
                MERCHANT_ID
        )).thenReturn(existingDish);

        when(categoryMapper.findById(
                dto.getCategoryId(),
                MERCHANT_ID
        )).thenReturn(category);

        assertThatThrownBy(() -> dishService.update(dto))
                .satisfies(exception ->
                        assertBusinessException(
                                exception,
                                ErrorCode.DISH_STATUS_INVALID,
                                ErrorMessage.DISH_STATUS_INVALID
                        )
                );

        verify(dishMapper)
                .findById(
                        dto.getId(),
                        MERCHANT_ID
                );

        verify(categoryMapper)
                .findById(
                        dto.getCategoryId(),
                        MERCHANT_ID
                );

        verify(dishMapper, never())
                .update(any(Dish.class));

        verify(redisService, never())
                .delete(anyString());
    }

    @Test
    void update_shouldRejectWhenDishNameAlreadyExists() {
        DishUpdateDTO dto = new DishUpdateDTO();
        dto.setId(1L);
        dto.setName("Existing Dish");
        dto.setCategoryId(2L);
        dto.setPrice(new BigDecimal("30.00"));
        dto.setStatus(DishStatus.ENABLED);

        Dish existingDish = new Dish();
        existingDish.setId(1L);
        existingDish.setName("Kung Pao Chicken");
        existingDish.setPrice(new BigDecimal("28.00"));
        existingDish.setCategoryId(1L);
        existingDish.setStatus(DishStatus.ENABLED);
        existingDish.setMerchantId(MERCHANT_ID);

        Category category = new Category();
        category.setId(2L);
        category.setName("Main Dishes");
        category.setType(CategoryType.DISH);
        category.setStatus(CategoryStatus.ENABLED);
        category.setMerchantId(MERCHANT_ID);

        when(dishMapper.findById(
                dto.getId(),
                MERCHANT_ID
        )).thenReturn(existingDish);

        when(categoryMapper.findById(
                dto.getCategoryId(),
                MERCHANT_ID
        )).thenReturn(category);

        when(dishMapper.update(any(Dish.class)))
                .thenThrow(new DuplicateKeyException("Duplicate dish name"));

        assertThatThrownBy(() -> dishService.update(dto))
                .satisfies(exception ->
                        assertBusinessException(
                                exception,
                                ErrorCode.DISH_NAME_ALREADY_EXISTS,
                                ErrorMessage.DISH_NAME_ALREADY_EXISTS
                        )
                );

        verify(dishMapper)
                .findById(
                        dto.getId(),
                        MERCHANT_ID
                );

        verify(categoryMapper)
                .findById(
                        dto.getCategoryId(),
                        MERCHANT_ID
                );

        verify(dishMapper)
                .update(any(Dish.class));

        verify(redisService, never())
                .delete(anyString());
    }

    @Test
    void update_shouldRejectWhenUpdateFails() {
        DishUpdateDTO dto = new DishUpdateDTO();
        dto.setId(1L);
        dto.setName("Updated Kung Pao Chicken");
        dto.setCategoryId(2L);
        dto.setPrice(new BigDecimal("30.00"));
        dto.setStatus(DishStatus.ENABLED);

        Dish existingDish = new Dish();
        existingDish.setId(1L);
        existingDish.setName("Kung Pao Chicken");
        existingDish.setPrice(new BigDecimal("28.00"));
        existingDish.setCategoryId(1L);
        existingDish.setStatus(DishStatus.ENABLED);
        existingDish.setMerchantId(MERCHANT_ID);

        Category category = new Category();
        category.setId(2L);
        category.setName("Main Dishes");
        category.setType(CategoryType.DISH);
        category.setStatus(CategoryStatus.ENABLED);
        category.setMerchantId(MERCHANT_ID);

        when(dishMapper.findById(
                dto.getId(),
                MERCHANT_ID
        )).thenReturn(existingDish);

        when(categoryMapper.findById(
                dto.getCategoryId(),
                MERCHANT_ID
        )).thenReturn(category);

        when(dishMapper.update(any(Dish.class)))
                .thenReturn(0);

        assertThatThrownBy(() -> dishService.update(dto))
                .satisfies(exception ->
                        assertBusinessException(
                                exception,
                                ErrorCode.DISH_UPDATE_FAILED,
                                ErrorMessage.DISH_UPDATE_FAILED
                        )
                );

        verify(dishMapper)
                .findById(
                        dto.getId(),
                        MERCHANT_ID
                );

        verify(categoryMapper)
                .findById(
                        dto.getCategoryId(),
                        MERCHANT_ID
                );

        verify(dishMapper)
                .update(any(Dish.class));

        verify(redisService, never())
                .delete(anyString());
    }

    @Test
    void update_shouldAllowPartialUpdate() {
        DishUpdateDTO dto = new DishUpdateDTO();
        dto.setId(1L);
        dto.setName("Updated Kung Pao Chicken");

        Dish existingDish = new Dish();
        existingDish.setId(1L);
        existingDish.setName("Kung Pao Chicken");
        existingDish.setPrice(new BigDecimal("28.00"));
        existingDish.setCategoryId(1L);
        existingDish.setStatus(DishStatus.ENABLED);
        existingDish.setMerchantId(MERCHANT_ID);

        when(dishMapper.findById(
                dto.getId(),
                MERCHANT_ID
        )).thenReturn(existingDish);

        when(dishMapper.update(any(Dish.class)))
                .thenReturn(1);

        dishService.update(dto);

        verify(dishMapper)
                .findById(
                        dto.getId(),
                        MERCHANT_ID
                );

        verify(categoryMapper, never())
                .findById(
                        anyLong(),
                        anyLong()
                );

        verify(dishMapper)
                .update(argThat(dish ->
                        dto.getId().equals(dish.getId())
                                && dto.getName().equals(dish.getName())
                                && dish.getPrice() == null
                                && dish.getCategoryId() == null
                                && dish.getStatus() == null
                                && MERCHANT_ID.equals(dish.getMerchantId())
                ));

        verify(redisService)
                .delete(anyString());
    }

    // ==================== Delete ====================
    @Test
    void deleteById_shouldDeleteDishSuccessfully() {
        Long dishId = 1L;

        Dish existingDish = new Dish();
        existingDish.setId(dishId);
        existingDish.setName("Kung Pao Chicken");
        existingDish.setPrice(new BigDecimal("28.00"));
        existingDish.setCategoryId(1L);
        existingDish.setStatus(DishStatus.ENABLED);
        existingDish.setMerchantId(MERCHANT_ID);

        when(dishMapper.findById(
                dishId,
                MERCHANT_ID
        )).thenReturn(existingDish);

        when(dishMapper.deleteById(
                dishId,
                MERCHANT_ID
        )).thenReturn(1);

        dishService.deleteById(dishId);

        verify(dishMapper)
                .findById(
                        dishId,
                        MERCHANT_ID
                );

        verify(dishMapper)
                .deleteById(
                        dishId,
                        MERCHANT_ID
                );

        verify(redisService)
                .delete(anyString());
    }

    @Test
    void deleteById_shouldRejectWhenDishNotFound() {
        Long dishId = 999L;

        when(dishMapper.findById(
                dishId,
                MERCHANT_ID
        )).thenReturn(null);

        assertThatThrownBy(() -> dishService.deleteById(dishId))
                .satisfies(exception ->
                        assertBusinessException(
                                exception,
                                ErrorCode.DISH_NOT_FOUND,
                                ErrorMessage.DISH_NOT_FOUND
                        )
                );

        verify(dishMapper)
                .findById(
                        dishId,
                        MERCHANT_ID
                );

        verify(dishMapper, never())
                .deleteById(
                        anyLong(),
                        anyLong()
                );

        verify(redisService, never())
                .delete(anyString());
    }

    @Test
    void deleteById_shouldRejectWhenDeleteFails() {
        Long dishId = 1L;

        Dish existingDish = new Dish();
        existingDish.setId(dishId);
        existingDish.setName("Kung Pao Chicken");
        existingDish.setPrice(new BigDecimal("28.00"));
        existingDish.setCategoryId(1L);
        existingDish.setStatus(DishStatus.ENABLED);
        existingDish.setMerchantId(MERCHANT_ID);

        when(dishMapper.findById(
                dishId,
                MERCHANT_ID
        )).thenReturn(existingDish);

        when(dishMapper.deleteById(
                dishId,
                MERCHANT_ID
        )).thenReturn(0);

        assertThatThrownBy(() -> dishService.deleteById(dishId))
                .satisfies(exception ->
                        assertBusinessException(
                                exception,
                                ErrorCode.DISH_DELETE_FAILED,
                                ErrorMessage.DISH_DELETE_FAILED
                        )
                );

        verify(dishMapper)
                .findById(
                        dishId,
                        MERCHANT_ID
                );

        verify(dishMapper)
                .deleteById(
                        dishId,
                        MERCHANT_ID
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