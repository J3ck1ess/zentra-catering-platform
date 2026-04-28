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

    void create (CategoryCreateDTO dto);

    PageResult<CategoryDTO> list(CategoryQueryDTO query);

    void deleteById(Long id);

    void update(CategoryUpdateDTO dto);
}
