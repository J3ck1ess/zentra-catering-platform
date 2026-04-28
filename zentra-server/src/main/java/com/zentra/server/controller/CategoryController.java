package com.zentra.server.controller;

import com.zentra.common.result.PageResult;
import com.zentra.common.result.Result;
import com.zentra.server.dto.CategoryCreateDTO;
import com.zentra.server.dto.CategoryDTO;
import com.zentra.server.dto.CategoryQueryDTO;
import com.zentra.server.dto.CategoryUpdateDTO;
import com.zentra.server.entity.Category;
import com.zentra.server.service.CategoryService;
import jakarta.validation.Valid;
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
     *
     * @param dto
     * @return
     */
    @PostMapping
    public Result<Void> create(@Valid @RequestBody CategoryCreateDTO dto) {

        categoryService.create(dto);

        return Result.success();
    }

    /**
     * Get categories with pagination and optional filters
     *
     * @param query
     * @return
     */
    @GetMapping
    public Result<PageResult<CategoryDTO>> list(@Valid CategoryQueryDTO query) {
        return Result.success(categoryService.list(query));
    }

    /**
     * Delete category by id
     *
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        categoryService.deleteById(id);
        return Result.success();
    }

    /**
     * Update category by id
     *
     * @param dto
     * @return
     */
    @PutMapping
    public Result<Void> update(@Valid @RequestBody CategoryUpdateDTO dto) {
        categoryService.update(dto);
        return Result.success();
    }
}
