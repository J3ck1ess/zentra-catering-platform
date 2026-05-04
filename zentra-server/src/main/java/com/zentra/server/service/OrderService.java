package com.zentra.server.service;

import com.zentra.common.result.PageResult;
import com.zentra.server.dto.OrderCreateDTO;
import com.zentra.server.dto.OrderDetailDTO;
import com.zentra.server.dto.OrderPageDTO;
import com.zentra.server.dto.OrderQueryDTO;

public interface OrderService {

    void create(OrderCreateDTO dto);

    PageResult<OrderPageDTO> list(OrderQueryDTO query);

    OrderDetailDTO getById(Long id);

    void updateStatus(Long id, Integer status);
}
