package com.zentra.server.service;

import com.zentra.common.result.PageResult;
import com.zentra.server.dto.DishCreateDTO;
import com.zentra.server.dto.DishDTO;
import com.zentra.server.dto.DishQueryDTO;
import com.zentra.server.dto.DishUpdateDTO;

/**
 * Service interface for Dish entity
 */
public interface DishService {

    /**
     * Create dish
     */
    void create(DishCreateDTO dto);

    /**
     * Query dishes with pagination
     */
    PageResult<DishDTO> page(DishQueryDTO query);

    /**
     * Update dish
     */
    void update(DishUpdateDTO dto);

    /**
     * Delete dish by id
     */
    void deleteById(Long id);


}
