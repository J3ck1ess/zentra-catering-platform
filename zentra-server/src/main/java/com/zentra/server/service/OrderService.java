package com.zentra.server.service;

import com.zentra.common.result.PageResult;
import com.zentra.server.dto.OrderCreateDTO;
import com.zentra.server.dto.OrderDetailDTO;
import com.zentra.server.dto.OrderPageDTO;
import com.zentra.server.dto.OrderQueryDTO;

public interface OrderService {

    /**
     * Create Order
     */
    void create(OrderCreateDTO dto);

    /**
     * Query orders with pagination
     */
    PageResult<OrderPageDTO> page(OrderQueryDTO query);

    /**
     * Get order detail by id
     */
    OrderDetailDTO getById(Long id);

    /**
     * Update order status
     */
    void updateStatus(Long id, Integer status);
}
