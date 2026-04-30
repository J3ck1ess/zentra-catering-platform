package com.zentra.server.mapper;


import com.zentra.server.entity.Order;
import org.apache.ibatis.annotations.Mapper;

/**
 * Mapper interface for order operations
 */
@Mapper
public interface OrderMapper {

    /**
     * Insert a new order
     *
     * @param order
     */
    void insert(Order order);
}
