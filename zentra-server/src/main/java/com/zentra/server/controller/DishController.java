package com.zentra.server.controller;

import com.zentra.common.result.PageResult;
import com.zentra.common.result.Result;
import com.zentra.server.dto.DishCreateDTO;
import com.zentra.server.dto.DishDTO;
import com.zentra.server.dto.DishQueryDTO;
import com.zentra.server.dto.DishUpdateDTO;
import com.zentra.server.service.DishService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for Dish APIs
 */
@RestController
@RequestMapping("/dish")
public class DishController {

    private final DishService dishService;

    public DishController(DishService dishService) {
        this.dishService = dishService;
    }

    /**
     * Create a new dish
     *
     * @param dto
     * @return
     */
    @PostMapping
    public Result<Void> create(@Valid @RequestBody DishCreateDTO dto) {

        dishService.create(dto);

        return Result.success();
    }

    /**
     * Get dishes with pagination and optional filters
     *
     * @param query
     * @return
     */
    @GetMapping
    public Result<PageResult<DishDTO>> list(@Valid DishQueryDTO query) {
        return Result.success(dishService.list(query));
    }

    /**
     * Delete dish by id
     *
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        dishService.delete(id);
        return Result.success();
    }

    /**
     * Update dish by id
     */
    @PutMapping
    public Result<Void> update(@Valid @RequestBody DishUpdateDTO dto) {
        dishService.update(dto);
        return Result.success();
    }
}
