package com.zentra.server.service.impl;

import com.zentra.common.constant.DishStatus;
import com.zentra.common.constant.ErrorCode;
import com.zentra.common.constant.ErrorMessage;
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
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * Implementation of DishService
 */
@Service
public class DishServiceImpl implements DishService {

    private final DishMapper dishMapper;
    private final CategoryMapper categoryMapper;

    public DishServiceImpl(DishMapper dishMapper, CategoryMapper categoryMapper) {
        this.dishMapper = dishMapper;
        this.categoryMapper = categoryMapper;
    }

    /**
     * Create dish
     *
     * @param dto
     */
    @Override
    public void create(DishCreateDTO dto) {

        Long merchantId = AuthContext.getCurrentMerchantId();

        // Validate category
        Category category = categoryMapper.findById(
                dto.getCategoryId(),
                merchantId
        );
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
    }

    /**
     * Query dishes with pagination
     *
     * @param query
     */
    @Override
    public PageResult<DishDTO> list(DishQueryDTO query) {

        Long merchantId = AuthContext.getCurrentMerchantId();

        // Calculate offset
        Integer page = query.getPage();
        Integer pageSize = query.getPageSize();
        int offset = (page - 1) * pageSize;

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

        return new PageResult<>(total, records);
    }

    /**
     * Update dish
     *
     * @param dto
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

        // Query dish
        Dish dbDish = dishMapper.findById(
                dto.getId(),
                merchantId
        );

        // Check dish existence
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

    }

    /**
     * Delete dish by id
     *
     * @param id
     */
    @Override
    public void deleteById(Long id) {

        Long merchantId = AuthContext.getCurrentMerchantId();

        // Query dish
        Dish dish = dishMapper.findById(
                id,
                merchantId
        );

        // Check dish existence
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

    }
}
