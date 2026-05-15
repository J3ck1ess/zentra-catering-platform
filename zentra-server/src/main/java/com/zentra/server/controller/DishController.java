package com.zentra.server.controller;

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

/**
 * Controller for Dish APIs
 */
@Tag(
        name = "Dish APIs",
        description = "Dish management APIs"
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
            description = "Create a new dish"
    )
    @SuccessApiResponse
    @ValidationErrorApiResponse
    @NotFoundApiResponse
    @AuthApiResponses
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
            description = "Retrieve paginated dish list with optional filters"
    )
    @DishPageApiResponse
    @AuthApiResponses
    @GetMapping
    public Result<PageResult<DishDTO>> list(
            @Valid DishQueryDTO query
    ) {

        return Result.success(
                dishService.list(query)
        );
    }

    /**
     * Update dish
     */
    @Operation(
            summary = "Update dish",
            description = "Update dish information"
    )
    @SuccessApiResponse
    @ValidationErrorApiResponse
    @NotFoundApiResponse
    @AuthApiResponses
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
            description = "Delete dish by dish id"
    )
    @SuccessApiResponse
    @NotFoundApiResponse
    @DependencyConflictApiResponse
    @AuthApiResponses
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @PathVariable Long id
    ) {

        dishService.deleteById(id);

        return Result.success();
    }

}
