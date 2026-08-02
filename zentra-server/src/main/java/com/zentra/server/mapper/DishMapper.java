package com.zentra.server.mapper;

import com.zentra.server.entity.Dish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Mapper interface for dish operations
 */
@Mapper
public interface DishMapper {

    /**
     * Insert a new dish
     */
    int insert(Dish dish);

    /**
     * Query dishes with pagination
     */
    List<Dish> findPage(
            @Param("name") String name,
            @Param("categoryId") Long categoryId,
            @Param("status") Integer status,
            @Param("merchantId") Long merchantId,
            @Param("offset") Integer offset,
            @Param("pageSize") Integer pageSize
    );

    /**
     * Count total dishes
     */
    Long count(
            @Param("name") String name,
            @Param("categoryId") Long categoryId,
            @Param("status") Integer status,
            @Param("merchantId") Long merchantId
    );

    /**
     * Delete dish by id
     */
    int deleteById(
            @Param("id") Long id,
            @Param("merchantId") Long merchantId
    );

    /**
     * Find dish by id
     */
    Dish findById(
            @Param("id") Long id,
            @Param("merchantId") Long merchantId
    );

    /**
     * Find enabled dishes by category
     */
    List<Dish> findEnabledDishes(
            @Param("categoryId") Long categoryId,
            @Param("merchantId") Long merchantId,
            @Param("status") Integer status
    );

    /**
     * Count dishes by category id
     */
    int countByCategoryId(
            @Param("categoryId") Long categoryId,
            @Param("merchantId") Long merchantId
    );

    /**
     * Update dish by id
     */
    int update(Dish dish);
}