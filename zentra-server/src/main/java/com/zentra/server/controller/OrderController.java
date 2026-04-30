package com.zentra.server.controller;


import com.zentra.common.result.Result;
import com.zentra.server.dto.OrderCreateDTO;
import com.zentra.server.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


}
