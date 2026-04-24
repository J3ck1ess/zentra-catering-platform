package com.zentra.server.service.impl;

import com.zentra.common.result.PageResult;
import com.zentra.server.context.UserContext;
import com.zentra.server.dto.DishCreateDTO;
import com.zentra.server.dto.DishDTO;
import com.zentra.server.dto.DishQueryDTO;
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
     * Create dish
     */
    @Override
    public void create(DishCreateDTO dto) {

        Dish dish = new Dish();
        dish.setName(dto.getName());
        dish.setPrice(dto.getPrice());
        dish.setCategoryId(dto.getCategoryId());
        dish.setStatus(dto.getStatus());

        // Set merchant ID from current user context
        dish.setMerchantId(UserContext.getCurrentUser());

        dishMapper.insert(dish);
    }

    /**
     * Query dishes with pagination
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
     */
    @Override
    public void delete(Long id) {

        if (id == null) {
            throw new IllegalArgumentException("Dish ID cannot be null");
        }

        dishMapper.deleteById(id);
    }
}
