package com.zentra.server.mapper;

import com.zentra.server.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Mapper interface for category operations
 */
@Mapper
public interface CategoryMapper {

    /**
     * Insert a new category
     */
    int insert(Category category);

    /**
     * Query categories with pagination
     */
    List<Category> findPage(
            @Param("name") String name,
            @Param("type") Integer type,
            @Param("status") Integer status,
            @Param("merchantId") Long merchantId,
            @Param("offset") Integer offset,
            @Param("pageSize") Integer pageSize
    );

    /**
     * Count total categories
     */
    Long count(
            @Param("name") String name,
            @Param("type") Integer type,
            @Param("status") Integer status,
            @Param("merchantId") Long merchantId
    );

    /**
     * Find enabled categories by merchant
     */
    List<Category> findEnabledCategories(
            @Param("merchantId") Long merchantId,
            @Param("status") Integer status
    );

    /**
     * Delete category by id
     */
    int deleteById(
            @Param("id") Long id,
            @Param("merchantId") Long merchantId
    );

    /**
     * Find category by id
     */
    Category findById(
            @Param("id") Long id,
            @Param("merchantId") Long merchantId
    );

    /**
     * Update category by id
     */
    int update(Category category);
}
