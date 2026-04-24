package com.zentra.server.service;

import com.zentra.common.result.PageResult;
import com.zentra.server.dto.DishDTO;
import com.zentra.server.entity.Dish;

import java.util.List;

/**
 * Service interface for Dish entity
 */
public interface DishService {

    void create(Dish dish);

    PageResult<DishDTO> list(Integer page, Integer pageSize, Long categoryId, Integer status);

    void delete(Long id);
}
