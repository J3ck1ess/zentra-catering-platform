package com.zentra.server.controller;

import com.zentra.common.result.PageResult;
import com.zentra.common.result.Result;
import com.zentra.server.dto.*;
import com.zentra.server.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for Order
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Create Order
     *
     * @param dto
     * @return
     */
    @PostMapping
    public Result<Void> create(@Valid @RequestBody OrderCreateDTO dto) {

        orderService.create(dto);
        return Result.success();
    }

    /**
     * Get orders with pagination and optional filters
     *
     * @param query
     * @return
     */
    @GetMapping
    public Result<PageResult<OrderPageDTO>> list(@Valid OrderQueryDTO query) {

        return Result.success(orderService.list(query));
    }

    /**
     * Get order details by id
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<OrderDetailDTO> getById(@PathVariable Long id) {

        return Result.success(orderService.getById(id));
    }

    /**
     * Update order status
     *
     * @param id
     * @param dto
     */
    @PatchMapping("/{id}/status")
    public Result<Void> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody OrderStatusUpdateDTO dto
    ) {

        orderService.updateStatus(id, dto.getStatus());
        return Result.success();
    }

}
