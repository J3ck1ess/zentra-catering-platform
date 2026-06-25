package com.zentra.server.controller;

import com.zentra.common.constant.PermissionConstants;
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
@Tag(
        name = "Order APIs",
        description =
                "Order management APIs with RBAC authorization"
)
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
            description =
                    "Create a new order with distributed" +
                    "duplicate request protection runtime"
    )
    @SuccessApiResponse
    @ValidationErrorApiResponse
    @NotFoundApiResponse
    @AuthApiResponses
    @DuplicateRequestApiResponse
    @AuditLog(
            operation = "CREATE_ORDER",
            resourceType = "order"
    )
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
            description =
                    "Retrieve paginated order list with optional filters. " +
                    "Requires permission: order:view"
    )
    @OrderPageApiResponse
    @AuthApiResponses
    @RequirePermission(
            PermissionConstants.ORDER_VIEW
    )
    @AuditLog(
            operation = "PAGE_ORDER",
            resourceType = "order"
    )
    @GetMapping
    public Result<PageResult<OrderPageDTO>> page(
            @Valid OrderQueryDTO query
    ) {

        return Result.success(
                orderService.page(query)
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
    @RequirePermission(
            PermissionConstants.ORDER_VIEW
    )
    @AuditLog(
            operation = "GET_ORDER",
            resourceType = "order"
    )
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
            description =
                    "Update order status by order id. " +
                    "Requires permission: order:update"
    )
    @SuccessApiResponse
    @ValidationErrorApiResponse
    @NotFoundApiResponse
    @AuthApiResponses
    @RequirePermission(
            PermissionConstants.ORDER_UPDATE
    )
    @AuditLog(
            operation = "UPDATE_ORDER_STATUS",
            resourceType = "order"
    )
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
