package com.zentra.server.mapper;

import com.zentra.server.dto.DishDTO;
import com.zentra.server.entity.Dish;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Mapper Interface for dish operation
 */
@Mapper
public interface DishMapper {

    /**
     * Insert a new dish
     */
    @Insert("INSERT INTO dish(name, price, category_id, status, merchant_id)" +
            "VALUES(#{name}, #{price}, #{categoryId}, #{status}, #{merchantId})")
    void insert(Dish dish);

    /**
     * Query dishes with join + pagination
     */
    @Select("""
            SELECT
                d.id,
                d.name,
                d.price,
                d.status,
                d.category_id AS categoryId,
                c.name AS categoryName
            FROM dish d
            LEFT JOIN category c ON d.category_id = c.id
            WHERE d.merchant_id = #{merchantId}
            AND (#{categoryId} IS NULL OR d.category_id = #{categoryId})
            AND (#{status} IS NULL OR d.status = #{status})
            ORDER BY d.id DESC
            LIMIT #{offset}, #{pageSize}
            """)
    List<DishDTO> findAll(
            @Param("categoryId") Long categoryId,
            @Param("status") Integer status,
            @Param("merchantId") Long merchantId,
            @Param("offset") Integer offset,
            @Param("pageSize") Integer pageSize
    );

    /**
     * Count total dishes
     */
    @Select("""
            SELECT COUNT(*)
            FROM dish d
            WHERE merchant_id = #{merchantId}
            AND (#{categoryId} IS NULL OR d.category_id = #{categoryId})
            AND (#{status} IS NULL OR d.status = #{status})
            """)
    Long count(
            @Param("categoryId") Long categoryId,
            @Param("status") Integer status,
            @Param("merchantId") Long merchantId
    );

    /**
     * Delete dish by id
     */
    @Delete("DELETE FROM dish WHERE id = #{id}")
    void deleteById(Long id);

    /**
     * Find dish by id
     */
    @Select("SELECT * FROM dish WHERE id = #{id}")
    Dish findById(Long id);

    /**
     * Update dish by id
     */
    void update(Dish dish);
}
