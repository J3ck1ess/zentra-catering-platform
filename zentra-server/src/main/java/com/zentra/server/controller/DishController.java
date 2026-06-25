package com.zentra.server.controller;

import com.zentra.common.constant.PermissionConstants;
import com.zentra.common.result.PageResult;
import com.zentra.common.result.Result;
import com.zentra.server.annotation.*;
import com.zentra.server.dto.DishCreateDTO;
import com.zentra.server.dto.DishDTO;
import com.zentra.server.dto.DishQueryDTO;
import com.zentra.server.dto.DishUpdateDTO;
import com.zentra.server.service.DishService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for Dish APIs
 */
@Tag(
        name = "Dish APIs",
        description = "Dish management APIs with RBAC permission control"
)
@RestController
@RequestMapping("/dish")
public class DishController {

    private final DishService dishService;

    public DishController(
            DishService dishService
    ) {

        this.dishService = dishService;
    }

    /**
     * Create dish
     */
    @Operation(
            summary = "Create dish",
            description =
                    "Create a new dish. " +
                    "Requires permission: dish:create"
    )
    @SuccessApiResponse
    @ValidationErrorApiResponse
    @NotFoundApiResponse
    @AuthApiResponses
    @RequirePermission(
            PermissionConstants.DISH_CREATE
    )
    @AuditLog(
            operation = "CREATE_DISH",
            resourceType = "dish"
    )
    @PostMapping
    public Result<Void> create(
            @Valid @RequestBody DishCreateDTO dto
    ) {

        dishService.create(dto);

        return Result.success();
    }

    /**
     * Get dishes with pagination and optional filters
     */
    @Operation(
            summary = "Get dish list",
            description =
                    "Retrieve paginated dish list with optional filters. " +
                    "Requires permission: dish:view"
    )
    @DishPageApiResponse
    @AuthApiResponses
    @RequirePermission(
            PermissionConstants.DISH_VIEW
    )
    @AuditLog(
            operation = "PAGE_DISH",
            resourceType = "dish"
    )
    @GetMapping
    public Result<PageResult<DishDTO>> page(
            @Valid DishQueryDTO query
    ) {

        return Result.success(
                dishService.page(query)
        );
    }

    /**
     * Get enabled dish list by category
     */
    @Operation(
            summary = "Get enabled dish list",
            description = "Retrieve enabled dishes for the specified category"
    )
    @SuccessApiResponse
    @AuthApiResponses
    @GetMapping("/list")
    public Result<List<DishDTO>> list(
            @RequestParam Long categoryId
    ) {

        return Result.success(
                dishService.list(categoryId)
        );
    }

    /**
     * Get dish detail by id
     */
    @Operation(
            summary = "Get dish detail",
            description = "Retrieve dish detail by dish id"
    )
    @SuccessApiResponse
    @NotFoundApiResponse
    @AuthApiResponses
    @AuditLog(
            operation = "GET_DISH",
            resourceType = "dish"
    )
    @GetMapping("/{id}")
    public Result<DishDTO> getById(
            @PathVariable Long id
    ) {

        return Result.success(
                dishService.getById(id)
        );
    }

    /**
     * Update dish
     */
    @Operation(
            summary = "Update dish",
            description =
                    "Update dish information. " +
                    "Requires permission: dish:update"
    )
    @SuccessApiResponse
    @ValidationErrorApiResponse
    @NotFoundApiResponse
    @AuthApiResponses
    @RequirePermission(
            PermissionConstants.DISH_UPDATE
    )
    @AuditLog(
            operation = "UPDATE_DISH",
            resourceType = "dish"
    )
    @PatchMapping
    public Result<Void> update(
            @Valid @RequestBody DishUpdateDTO dto
    ) {

        dishService.update(dto);

        return Result.success();
    }

    /**
     * Delete dish by id
     */
    @Operation(
            summary = "Delete dish",
            description =
                    "Delete dish by dish id. " +
                    "Requires permission: dish:delete"
    )
    @SuccessApiResponse
    @NotFoundApiResponse
    @DependencyConflictApiResponse
    @AuthApiResponses
    @RequirePermission(
            PermissionConstants.DISH_DELETE
    )
    @AuditLog(
            operation = "DELETE_DISH",
            resourceType = "dish"
    )
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @PathVariable Long id
    ) {

        dishService.deleteById(id);

        return Result.success();
    }

}
