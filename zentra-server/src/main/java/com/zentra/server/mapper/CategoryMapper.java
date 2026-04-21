package com.zentra.server.mapper;

import com.zentra.server.entity.Category;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Mapper interface for category operations
 */
@Mapper
public interface CategoryMapper {
    /**
     * Insert a new category
     */
    @Insert("INSERT INTO category(name, type, status, merchant_id) " +
            "VALUES(#{name}, #{type}, #{status}, #{merchantId})")
    void insert(Category category);

    /**
     * Query all categories
     */
    @Select("SELECT * FROM category ORDER BY sort ASC")
    List<Category> findAll();

    /**
     * Delete a category by id
     */
    @Delete("DELETE FROM category WHERE id = #{id}")
    void deleteById(Long id);
}
