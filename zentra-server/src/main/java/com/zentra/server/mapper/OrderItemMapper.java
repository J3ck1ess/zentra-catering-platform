package com.zentra.server.mapper;

import com.zentra.server.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;

/**
 * Mapper interface for OrderItem
 */
@Mapper
public interface OrderItemMapper {

    /**
     * Insert order item
     *
     * @param orderItem
     */
    void insert(OrderItem orderItem);
}
