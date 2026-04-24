package com.zentra.server.service;

import com.zentra.common.result.PageResult;
import com.zentra.server.dto.DishCreateDTO;
import com.zentra.server.dto.DishDTO;
import com.zentra.server.dto.DishQueryDTO;
import com.zentra.server.entity.Dish;

import java.util.List;

/**
 * Service interface for Dish entity
 */
public interface DishService {

    void create(DishCreateDTO dto);

    PageResult<DishDTO> list(DishQueryDTO query);

    void delete(Long id);
}
