package com.zentra.server.mapper;


import com.zentra.server.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Mapper interface for order operations
 */
@Mapper
public interface OrderMapper {

    /**
     * Insert a new order
     */
    int insert(Order order);

    /**
     * Query orders with pagination
     */
    List<Order> findPage(
            @Param("status") Integer status,
            @Param("merchantId") Long merchantId,
            @Param("offset") Integer offset,
            @Param("pageSize") Integer pageSize
    );

    /**
     * Count total orders
     */
    Long count(
            @Param("status") Integer status,
            @Param("merchantId") Long merchantId
    );

    /**
     * Find order by id
     */
    Order findById(
            @Param("id") Long id,
            @Param("merchantId") Long merchantId
    );

    /**
     * Update order status
     */
    int updateStatus(
            @Param("id") Long id,
            @Param("merchantId") Long merchantId,
            @Param("status") Integer status
    );


}
