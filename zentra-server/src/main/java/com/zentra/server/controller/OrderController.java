package com.zentra.server.controller;

import com.zentra.common.result.PageResult;
import com.zentra.common.result.Result;
import com.zentra.server.annotation.*;
import com.zentra.server.dto.*;
import com.zentra.server.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for order APIs
 */
@Tag(name = "Order APIs")
@RestController
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(
            OrderService orderService
    ) {

        this.orderService = orderService;
    }

    /**
     * Create Order
     */
    @Operation(
            summary = "Create Order",
            description = "Create a new order"
    )
    @SuccessApiResponse
    @ValidationErrorApiResponse
    @NotFoundApiResponse
    @AuthApiResponses
    @PostMapping
    public Result<Void> create(
            @Valid @RequestBody OrderCreateDTO dto
    ) {

        orderService.create(dto);

        return Result.success();
    }

    /**
     * Get orders with pagination and optional filters
     */
    @Operation(
            summary = "Get order list",
            description = "Retrieve paginated order list with optional filters"
    )
    @OrderPageApiResponse
    @AuthApiResponses
    @GetMapping
    public Result<PageResult<OrderPageDTO>> list(
            @Valid OrderQueryDTO query
    ) {

        return Result.success(
                orderService.list(query)
        );
    }

    /**
     * Get order details by id
     */
    @Operation(
            summary = "Get order by id",
            description = "Retrieve order details by order id"
    )
    @OrderDetailApiResponse
    @NotFoundApiResponse
    @AuthApiResponses
    @GetMapping("/{id}")
    public Result<OrderDetailDTO> getById(
            @PathVariable Long id
    ) {

        return Result.success(
                orderService.getById(id)
        );
    }

    /**
     * Update order status
     */
    @Operation(
            summary = "Update order status",
            description = "Update order status by order id"
    )
    @SuccessApiResponse
    @ValidationErrorApiResponse
    @NotFoundApiResponse
    @AuthApiResponses
    @PatchMapping("/{id}/status")
    public Result<Void> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody OrderStatusUpdateDTO dto
    ) {

        orderService.updateStatus(
                id,
                dto.getStatus()
        );

        return Result.success();
    }

}
