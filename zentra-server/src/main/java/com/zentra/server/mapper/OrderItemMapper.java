package com.zentra.server.mapper;

import com.zentra.server.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Mapper interface for OrderItem
 */
@Mapper
public interface OrderItemMapper {

    /**
     * Insert order item
     */
    int insert(OrderItem orderItem);

    /**
     * Query order items by order id
     */
    List<OrderItem> findByOrderId(
            @Param("orderId") Long orderId,
            @Param("merchantId") Long merchantId
    );
}
