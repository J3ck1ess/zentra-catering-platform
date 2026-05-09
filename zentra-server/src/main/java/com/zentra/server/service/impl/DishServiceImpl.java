package com.zentra.server.service.impl;

import com.zentra.common.constant.DishStatus;
import com.zentra.common.result.PageResult;
import com.zentra.common.util.AssertUtil;
import com.zentra.common.context.UserContext;
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

        Long merchantId = UserContext.getCurrentUser();

        // Validate category
        Category category = categoryMapper.findById(
                dto.getCategoryId(),
                merchantId
        );
        AssertUtil.notNull(category, "Category not found");

        // Convert DTO to entity
        Dish dish = new Dish();
        BeanUtils.copyProperties(dto, dish);

        // Default dish status
        dish.setStatus(DishStatus.ENABLED);

        // Set merchant ID from current user context
        dish.setMerchantId(merchantId);

        int rows = dishMapper.insert(dish);
        AssertUtil.checkRows(rows, "Failed to create a dish");
    }

    /**
     * Query dishes with pagination
     *
     * @param query
     */
    @Override
    public PageResult<DishDTO> list(DishQueryDTO query) {

        Long merchantId = UserContext.getCurrentUser();

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

        AssertUtil.notNull(dto.getId(), "Dish id cannot be null");

        if (dto.getName() == null
                && dto.getPrice() == null
                && dto.getCategoryId() == null
                && dto.getStatus() == null) {

            throw new IllegalArgumentException("No fields to update");
        }

        Long merchantId = UserContext.getCurrentUser();

        // Validate category
        if (dto.getCategoryId() != null) {
            Category category = categoryMapper.findById(
                    dto.getCategoryId(),
                    merchantId
            );

            AssertUtil.notNull(category, "Category not found");
        }

        // Validate dish status
        if (dto.getStatus() != null
                && !Objects.equals(dto.getStatus(), DishStatus.ENABLED)
                && !Objects.equals(dto.getStatus(), DishStatus.DISABLED)) {

            throw new IllegalArgumentException("Invalid dish status");
        }

        // Convert DTO to entity
        Dish dish = new Dish();
        BeanUtils.copyProperties(dto, dish);

        dish.setMerchantId(merchantId);

        int rows = dishMapper.update(dish);
        AssertUtil.checkRows(rows, "Dish not found or no permission");

    }

    /**
     * Delete dish by id
     *
     * @param id
     */
    @Override
    public void deleteById(Long id) {

        // Validate input parameter
        AssertUtil.notNull(id, "Dish id cannot be null");

        Long merchantId = UserContext.getCurrentUser();

        // Delete dish with merchant scope restriction
        int rows = dishMapper.deleteById(id, merchantId);
        AssertUtil.checkRows(rows, "Dish not found or no permission");

    }
}
