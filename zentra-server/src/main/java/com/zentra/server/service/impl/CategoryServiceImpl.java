package com.zentra.server.service.impl;

import com.zentra.server.context.UserContext;
import com.zentra.server.entity.Category;
import com.zentra.server.mapper.CategoryMapper;
import com.zentra.server.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of CategoryService
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    /**
     * Create a new category
     */
    @Override
    public void create(Category category) {

        // Set merchant ID from current user context
        category.setMerchantId(UserContext.getCurrentUser());

        categoryMapper.insert(category);
    }

    /**
     * Get all categories
     */
    @Override
    public List<Category> list() {
        return categoryMapper.findAll();
    }

    /**
     * Delete category by id
     */
    @Override
    public void delete(Long id) {
        categoryMapper.deleteById(id);
    }

}
