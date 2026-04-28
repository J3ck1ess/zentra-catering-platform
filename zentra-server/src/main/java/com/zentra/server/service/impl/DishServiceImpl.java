package com.zentra.server.service.impl;

import com.zentra.common.result.PageResult;
import com.zentra.common.util.AssertUtil;
import com.zentra.server.context.UserContext;
import com.zentra.server.dto.DishCreateDTO;
import com.zentra.server.dto.DishDTO;
import com.zentra.server.dto.DishQueryDTO;
import com.zentra.server.dto.DishUpdateDTO;
import com.zentra.server.entity.Dish;
import com.zentra.server.mapper.DishMapper;
import com.zentra.server.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of DishService
 */
@Service
public class DishServiceImpl implements DishService {

    private final DishMapper dishMapper;

    public DishServiceImpl(DishMapper dishMapper) {
        this.dishMapper = dishMapper;
    }

    /**
     * Create dish
     *
     * @param dto
     */
    @Override
    public void create(DishCreateDTO dto) {

        // Convert DTO to entity
        Dish dish = new Dish();
        BeanUtils.copyProperties(dto, dish);

        // Set merchant ID from current user context
        dish.setMerchantId(UserContext.getCurrentUser());

        dishMapper.insert(dish);
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
        List<DishDTO> records = dishMapper.findAll(
                query.getCategoryId(),
                query.getStatus(),
                merchantId,
                offset,
                pageSize
        );

        // Query total count
        Long total = dishMapper.count(
                query.getCategoryId(),
                query.getStatus(),
                merchantId
        );

        return new PageResult<>(total, records);
    }

    /**
     * Delete dish by id
     *
     * @param id
     */
    @Override
    public void delete(Long id) {

        // Validate input parameter
        AssertUtil.notNull(id, "Dish id cannot be null");

        Long merchantId = UserContext.getCurrentUser();

        // Delete dish with merchant scope restriction
        int rows = dishMapper.deleteById(id, merchantId);
        AssertUtil.checkRows(rows, "Dish not found or no permission");
    }

    /**
     * Update dish by id
     *
     * @param dto
     */
    @Override
    public void update(DishUpdateDTO dto) {

        AssertUtil.notNull(dto.getId(), "Dish id cannot be null");

        if (dto.getName() == null &&
                dto.getPrice() == null &&
                dto.getCategoryId() == null &&
                dto.getStatus() == null) {

            throw new IllegalArgumentException("No fields to update");
        }

        // Convert DTO to entity
        Dish dish = new Dish();
        BeanUtils.copyProperties(dto, dish);

        dish.setMerchantId(UserContext.getCurrentUser());

        int rows = dishMapper.update(dish);
        AssertUtil.checkRows(rows, "Dish not found or no permission");

    }
}
