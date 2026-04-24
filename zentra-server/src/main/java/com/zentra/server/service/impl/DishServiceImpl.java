package com.zentra.server.service.impl;

import com.zentra.common.result.PageResult;
import com.zentra.server.context.UserContext;
import com.zentra.server.dto.DishDTO;
import com.zentra.server.entity.Dish;
import com.zentra.server.mapper.DishMapper;
import com.zentra.server.service.DishService;
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
     * Create a new dish
     */
    @Override
    public void create(Dish dish) {

        // Set merchant ID from current user context
        dish.setMerchantId(UserContext.getCurrentUser());

        dishMapper.insert(dish);
    }

    /**
     * Query dishes with pagination
     */
    @Override
    public PageResult<DishDTO> list(Integer page, Integer pageSize, Long categoryId, Integer status) {

        Long merchantId = UserContext.getCurrentUser();

        // Calculate offset
        int offset = (page - 1) * pageSize;

        // Query data
        List<DishDTO> records = dishMapper.findAll(categoryId, status, merchantId, offset, pageSize);

        // Query total count
        Long total = dishMapper.count(categoryId, status, merchantId);

        return new PageResult<>(total, records);
    }

    /**
     * Delete dish by id
     */
    @Override
    public void delete(Long id) {
        dishMapper.deleteById(id);
    }
}
