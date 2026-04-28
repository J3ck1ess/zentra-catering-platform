package com.zentra.server.mapper;

import com.zentra.server.dto.CategoryDTO;
import com.zentra.server.entity.Category;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Mapper interface for category operations
 */
@Mapper
public interface CategoryMapper {
    /**
     * Insert a new category
     */
    @Insert("INSERT INTO category(name, type, status, sort, merchant_id) " +
            "VALUES(#{name}, #{type}, #{status}, #{sort}, #{merchantId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Category category);

    /**
     * Query all categories
     */
    @Select("""
            SELECT *
            FROM category c
            WHERE c.merchant_id = #{merchantId}
            AND (#{type} IS NULL OR c.type = #{type})
            AND (#{status} IS NULL OR c.status = #{status})
            ORDER BY c.sort ASC
            LIMIT #{offset}, #{pageSize}
            """)

    List<CategoryDTO> findAll(
            @Param("type") Integer type,
            @Param("status") Integer status,
            @Param("merchantId") Long merchantId,
            @Param("offset") Integer offset,
            @Param("pageSize") Integer pageSize
    );

    /**
     * Count total categories
     */
    @Select("""
            SELECT COUNT(*)
            FROM category c
            WHERE c.merchant_id = #{merchantId}
            AND (#{type} IS NULL OR c.type = #{type})
            AND (#{status} IS NULL OR c.status = #{status})
            """)
    Long count(
            @Param("type") Integer type,
            @Param("status") Integer status,
            @Param("merchantId") Long merchantId
    );


    /**
     * Delete a category by id
     */
    @Delete("""
        DELETE FROM category 
        WHERE id = #{id}
        AND merchant_id = #{merchantId}
    """)
    int deleteById(Long id, Long merchantId);

    /**
     * Find a category by id
     */
    @Select("SELECT * FROM category WHERE id = #{id} AND merchant_id = #{merchantId}")
    Category findById(Long id, Long merchantId);


    /**
     * Update category by id
     */
    int update(Category category);
}
