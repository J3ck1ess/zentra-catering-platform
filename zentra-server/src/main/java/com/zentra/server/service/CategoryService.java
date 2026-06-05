package com.zentra.server.service;

import com.zentra.common.result.PageResult;
import com.zentra.server.dto.CategoryCreateDTO;
import com.zentra.server.dto.CategoryDTO;
import com.zentra.server.dto.CategoryQueryDTO;
import com.zentra.server.dto.CategoryUpdateDTO;
import com.zentra.server.entity.Category;
import jakarta.validation.Valid;

import java.util.List;

/**
 * Service interface for category business logic
 */
public interface CategoryService {

    /**
     * Create category
     */
    void create (CategoryCreateDTO dto);

    /**
     * Query categories with pagination
     */
    PageResult<CategoryDTO> page(CategoryQueryDTO query);

    /**
     * Get enabled category list for current merchant
     */
    List<CategoryDTO> list();

    /**
     * Update category
     */
    void update(CategoryUpdateDTO dto);

    /**
     * Delete category by id
     */
    void deleteById(Long id);
}
