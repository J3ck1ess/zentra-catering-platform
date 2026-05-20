package com.zentra.server.controller;

import com.zentra.common.constant.PermissionConstants;
import com.zentra.common.result.PageResult;
import com.zentra.common.result.Result;
import com.zentra.server.annotation.*;
import com.zentra.server.dto.CategoryCreateDTO;
import com.zentra.server.dto.CategoryDTO;
import com.zentra.server.dto.CategoryQueryDTO;
import com.zentra.server.dto.CategoryUpdateDTO;
import com.zentra.server.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for category APIs
 */
@Tag(
        name = "Category APIs",
        description =
                "Category management APIs with RBAC permission control"
)
@RestController
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(
            CategoryService categoryService
    ) {

        this.categoryService = categoryService;
    }

    /**
     * Create category
     */
    @Operation(
            summary = "Create category",
            description =
                    "Create a new category. " +
                    "Requires permission: category:create"
    )
    @SuccessApiResponse
    @ValidationErrorApiResponse
    @ConflictApiResponse
    @AuthApiResponses
    @RequirePermission(
            PermissionConstants.CATEGORY_CREATE
    )
    @PostMapping
    public Result<Void> create(
            @Valid @RequestBody CategoryCreateDTO dto
    ) {

        categoryService.create(dto);

        return Result.success();
    }

    /**
     * Get categories with pagination and optional filters
     */
    @Operation(
            summary = "Get category list",
            description =
                    "Retrieve paginated category list with optional filters. " +
                    "Requires permission: category:view"
    )
    @CategoryPageApiResponse
    @AuthApiResponses
    @RequirePermission(
            PermissionConstants.CATEGORY_VIEW
    )
    @GetMapping
    public Result<PageResult<CategoryDTO>> page(
            @Valid CategoryQueryDTO query
    ) {

        return Result.success(
                categoryService.page(query)
        );
    }

    /**
     * Update category
     */
    @Operation(
            summary = "Update category",
            description =
                    "Update category information. " +
                    "Requires permission: category:update"
    )
    @SuccessApiResponse
    @ValidationErrorApiResponse
    @NotFoundApiResponse
    @AuthApiResponses
    @RequirePermission(
            PermissionConstants.CATEGORY_UPDATE
    )
    @PatchMapping
    public Result<Void> update(
            @Valid @RequestBody CategoryUpdateDTO dto
    ) {

        categoryService.update(dto);

        return Result.success();
    }

    /**
     * Delete category by id
     */
    @Operation(
            summary = "Delete category",
            description =
                    "Delete category by category id. " +
                    "Requires permission: category:delete"
    )
    @SuccessApiResponse
    @NotFoundApiResponse
    @DependencyConflictApiResponse
    @AuthApiResponses
    @RequirePermission(
            PermissionConstants.CATEGORY_DELETE
    )
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @PathVariable Long id
    ) {

        categoryService.deleteById(id);

        return Result.success();
    }
}
