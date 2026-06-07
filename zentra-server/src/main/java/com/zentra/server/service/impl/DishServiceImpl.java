package com.zentra.server.service.impl;

import com.zentra.common.constant.*;
import com.zentra.common.context.AuthContext;
import com.zentra.common.exception.BusinessException;
import com.zentra.common.result.PageResult;
import com.zentra.common.util.AssertUtil;
import com.zentra.server.dto.*;
import com.zentra.server.entity.Category;
import com.zentra.server.entity.Dish;
import com.zentra.server.mapper.CategoryMapper;
import com.zentra.server.mapper.DishMapper;
import com.zentra.server.service.DishService;
import com.zentra.server.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

/**
 * Implementation of DishService
 */
@Service
@Slf4j
public class DishServiceImpl implements DishService {

    private final DishMapper dishMapper;
    private final CategoryMapper categoryMapper;
    private final RedisService redisService;

    public DishServiceImpl(
            DishMapper dishMapper,
            CategoryMapper categoryMapper,
            RedisService redisService
    ) {
        this.dishMapper = dishMapper;
        this.categoryMapper = categoryMapper;
        this.redisService = redisService;
    }

    // Build dish detail cache key
    private String buildDishDetailCacheKey(Long dishId) {

        return RedisKeyConstants.DISH_DETAIL_CACHE + dishId;
    }

    /**
     * Create dish
     */
    @Override
    public void create(DishCreateDTO dto) {

        Long merchantId = AuthContext.getCurrentMerchantId();

        log.info(
                "[DISH] Dish creation started. merchantId={}, name={}, categoryId={}",
                merchantId,
                dto.getName(),
                dto.getCategoryId()
        );

        // Validate category
        Category category = categoryMapper.findById(
                dto.getCategoryId(),
                merchantId
        );
        if (category == null) {
            log.warn(
                    "[DISH] Category not found during dish creation. merchantId={}, categoryId={}",
                    merchantId,
                    dto.getCategoryId()
            );
        }
        AssertUtil.notNull(
                category,
                ErrorCode.CATEGORY_NOT_FOUND,
                ErrorMessage.CATEGORY_NOT_FOUND
        );

        // Convert DTO to Entity
        Dish dish = new Dish();
        BeanUtils.copyProperties(dto, dish);

        // Default dish status
        dish.setStatus(DishStatus.ENABLED);

        // Set merchant ID from current user context
        dish.setMerchantId(merchantId);

        int rows = dishMapper.insert(dish);
        AssertUtil.checkRows(
                rows,
                ErrorCode.DISH_CREATE_FAILED,
                ErrorMessage.DISH_CREATE_FAILED
        );

        log.info(
                "[DISH] Dish created successfully. merchantId={}, name={}",
                merchantId,
                dto.getName()
        );
    }

    /**
     * Query dishes with pagination
     */
    @Override
    public PageResult<DishDTO> page(DishQueryDTO query) {

        Long merchantId = AuthContext.getCurrentMerchantId();

        // Calculate offset
        Integer page = query.getPage();
        Integer pageSize = query.getPageSize();
        int offset = (page - 1) * pageSize;

        log.info(
                "[DISH] Dish query started. merchantId={}, page={}, pageSize={}, categoryId={}, status={}",
                merchantId,
                page,
                pageSize,
                query.getCategoryId(),
                query.getStatus()
        );

        // Query data
        List<Dish> list = dishMapper.findPage(
                query.getCategoryId(),
                query.getStatus(),
                merchantId,
                offset,
                pageSize
        );

        // Convert Entity -> DTO
        List<DishDTO> records = list.stream().map(dish -> {

            DishDTO dto = new DishDTO();
            BeanUtils.copyProperties(dish, dto);

            return dto;
        }).toList();

        // Query total count
        Long total = dishMapper.count(
                query.getCategoryId(),
                query.getStatus(),
                merchantId
        );

        log.info(
                "[DISH] Dish page query completed. merchantId={}, total={}",
                merchantId,
                total
        );
        return new PageResult<>(total, records);
    }

    /**
     * Get enabled dish list by category
     */
    @Override
    public List<DishDTO> list(Long categoryId) {

        AssertUtil.notNull(
                categoryId,
                ErrorCode.CATEGORY_ID_REQUIRED,
                ErrorMessage.CATEGORY_ID_REQUIRED
        );

        Long merchantId = AuthContext.getCurrentMerchantId();

        log.info(
                "[DISH] Dish list query started. merchantId={}, categoryId={}",
                merchantId,
                categoryId
        );
        List<Dish> dishes = dishMapper.findEnabledDishes(
                categoryId,
                merchantId,
                DishStatus.ENABLED
        );

        List<DishDTO> result = dishes.stream().map(dish -> {

            DishDTO dto = new DishDTO();
            BeanUtils.copyProperties(dish, dto);

            return dto;
        }).toList();

        log.info(
                "[DISH] Dish list query completed. merchantId={}, categoryId={}, dishCount={}",
                merchantId,
                categoryId,
                result.size()
        );

        return result;
    }

    /**
     * Get dish detail by id
     */
    @Override
    public DishDTO getById(Long id) {

        AssertUtil.notNull(
                id,
                ErrorCode.DISH_ID_REQUIRED,
                ErrorMessage.DISH_ID_REQUIRED
        );

        Long merchantId = AuthContext.getCurrentMerchantId();

        log.info(
                "[DISH] Dish detail query started. merchantId={}, dishId={}",
                merchantId,
                id
        );

        DishDTO cachedDish =
                redisService.get(
                        buildDishDetailCacheKey(id),
                        DishDTO.class
                );

        if (cachedDish != null) {

            log.info(
                    "[DISH_CACHE] Dish detail cache hit. dishId={}",
                    id
            );

            return cachedDish;
        }

        log.info(
                "[DISH_CACHE] Dish detail cache miss. dishId={}",
                id
        );

        // Query database
        Dish dish = dishMapper.findById(
                id,
                merchantId
        );

        if (dish == null) {
            log.warn(
                    "[DISH] Dish not found during detail query. merchantId={}, dishId={}",
                    merchantId,
                    id
            );
        }
        AssertUtil.notNull(
                dish,
                ErrorCode.DISH_NOT_FOUND,
                ErrorMessage.DISH_NOT_FOUND
        );

        // Convert Entity -> DTO
        DishDTO dto = new DishDTO();
        BeanUtils.copyProperties(dish, dto);

        // Write cache
        redisService.set(
                buildDishDetailCacheKey(id),
                dto,
                RedisTtlConstants.DISH_DETAIL_CACHE_TTL
        );

        log.info(
                "[DISH_CACHE] Dish detail cached. dishId={}",
                id
        );

        log.info(
                "[DISH] Dish detail query completed. merchantId={}, dishId={}",
                merchantId,
                id
        );

        return dto;
    }

    /**
     * Update dish
     */
    @Override
    public void update(DishUpdateDTO dto) {

        // Check if update fields are empty
        if (dto.getName() == null
                && dto.getPrice() == null
                && dto.getCategoryId() == null
                && dto.getStatus() == null) {

            throw new BusinessException(
                    ErrorCode.DISH_UPDATE_FAILED,
                    ErrorMessage.DISH_UPDATE_FAILED
            );
        }

        Long merchantId = AuthContext.getCurrentMerchantId();

        log.info(
                "[DISH] Dish update started. merchantId={}, dishId={}",
                merchantId,
                dto.getId()
        );

        // Query dish
        Dish dbDish = dishMapper.findById(
                dto.getId(),
                merchantId
        );

        // Check dish existence
        if (dbDish == null) {
            log.warn(
                    "[DISH] Dish not found during update. merchantId={}, dishId={}",
                    merchantId,
                    dto.getId()
            );
        }
        AssertUtil.notNull(
                dbDish,
                ErrorCode.DISH_NOT_FOUND,
                ErrorMessage.DISH_NOT_FOUND
        );

        // Validate category
        if (dto.getCategoryId() != null) {
            Category category = categoryMapper.findById(
                    dto.getCategoryId(),
                    merchantId
            );

            if (category == null) {
                log.warn(
                        "[DISH] Category not found during dish update. merchantId={}, categoryId={}",
                        merchantId,
                        dto.getCategoryId()
                );
            }
            AssertUtil.notNull(
                    category,
                    ErrorCode.CATEGORY_NOT_FOUND,
                    ErrorMessage.CATEGORY_NOT_FOUND
            );
        }

        // Validate dish status
        if (dto.getStatus() != null
                && !Objects.equals(dto.getStatus(), DishStatus.ENABLED)
                && !Objects.equals(dto.getStatus(), DishStatus.DISABLED)) {

            throw new BusinessException(
                    ErrorCode.DISH_STATUS_INVALID,
                    ErrorMessage.DISH_STATUS_INVALID
            );
        }

        // Convert DTO to Entity
        Dish dish = new Dish();
        BeanUtils.copyProperties(dto, dish);

        dish.setMerchantId(merchantId);

        int rows = dishMapper.update(dish);
        AssertUtil.checkRows(
                rows,
                ErrorCode.DISH_UPDATE_FAILED,
                ErrorMessage.DISH_UPDATE_FAILED
        );

        // Delete cache
        redisService.delete(buildDishDetailCacheKey(dto.getId()));

        log.info(
                "[DISH_CACHE] Dish detail cache evicted after update. dishId={}",
                dto.getId()
        );

        log.info(
                "[DISH] Dish updated successfully. merchantId={}, dishId={}",
                merchantId,
                dto.getId()
        );

    }

    /**
     * Delete dish by id
     */
    @Override
    public void deleteById(Long id) {

        Long merchantId = AuthContext.getCurrentMerchantId();

        log.info(
                "[DISH] Dish deletion started. merchantId={}, dishId={}",
                merchantId,
                id
        );

        // Query dish
        Dish dish = dishMapper.findById(
                id,
                merchantId
        );

        // Check dish existence
        if (dish == null) {
            log.warn(
                    "[DISH] Dish not found during deletion. merchantId={}, dishId={}",
                    merchantId,
                    id
            );
        }
        AssertUtil.notNull(
                dish,
                ErrorCode.DISH_NOT_FOUND,
                ErrorMessage.DISH_NOT_FOUND
        );

        // Delete dish
        int rows = dishMapper.deleteById(
                id,
                merchantId
        );

        AssertUtil.checkRows(
                rows,
                ErrorCode.DISH_DELETE_FAILED,
                ErrorMessage.DISH_DELETE_FAILED
        );

        // Delete cache
        redisService.delete(buildDishDetailCacheKey(id));

        log.info(
                "[DISH_CACHE] Dish detail cache evicted after deletion. dishId={}",
                id
        );

        log.info(
                "[DISH] Dish deleted successfully. merchantId={}, dishId={}",
                merchantId,
                id
        );

    }
}
