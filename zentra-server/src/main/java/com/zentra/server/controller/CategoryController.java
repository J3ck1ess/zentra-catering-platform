package com.zentra.server.controller;

import com.zentra.common.result.Result;
import com.zentra.server.entity.Category;
import com.zentra.server.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for category APIs
 */
@RestController
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Create category
     */
    @PostMapping
    public Result<Void> create(@RequestBody Category category) {
        categoryService.create(category);
        return Result.success();
    }

    /**
     * Get all categories
     */
    @GetMapping
    public Result<List<Category>> list() {
        return Result.success(categoryService.list());
    }

    /**
     * Delete category by id
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return Result.success();
    }
}
