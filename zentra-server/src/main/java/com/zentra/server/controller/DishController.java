package com.zentra.server.controller;

import com.zentra.common.result.PageResult;
import com.zentra.common.result.Result;
import com.zentra.server.dto.DishDTO;
import com.zentra.server.entity.Dish;
import com.zentra.server.service.DishService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
     * @param dish
     * @return
     */
    @PostMapping
    public Result<Void> create(@RequestBody Dish dish) {
        dishService.create(dish);
        if (dish.getName() == null || dish.getPrice() == null){
            throw new RuntimeException("Invalid dish data");
        }
        return Result.success();
    }

    /**
     * Get dishes with pagination and optional filters
     *
     * @param page page number
     * @param pageSize page size
     * @param categoryId optional category filter
     * @param status optional status filter
     * @return paginated dish list
     */
    @GetMapping
    public Result<PageResult<DishDTO>> list(

            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer status
    ) {
        return Result.success(
                dishService.list(page, pageSize, categoryId, status)
        );
    }

    /**
     * Delete dish by id
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        dishService.delete(id);
        return Result.success();
    }
}
