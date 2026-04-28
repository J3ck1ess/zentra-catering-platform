package com.zentra.server.service.impl;

import com.zentra.common.result.PageResult;
import com.zentra.common.util.AssertUtil;
import com.zentra.server.context.UserContext;
import com.zentra.server.dto.CategoryCreateDTO;
import com.zentra.server.dto.CategoryDTO;
import com.zentra.server.dto.CategoryQueryDTO;
import com.zentra.server.dto.CategoryUpdateDTO;
import com.zentra.server.entity.Category;
import com.zentra.server.mapper.CategoryMapper;
import com.zentra.server.mapper.DishMapper;
import com.zentra.server.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of CategoryService
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;

    private final DishMapper dishMapper;

    public CategoryServiceImpl(CategoryMapper categoryMapper, DishMapper dishMapper) {
        this.categoryMapper = categoryMapper;
        this.dishMapper = dishMapper;
    }

    /**
     * Create a new category
     *
     * @param dto
     */
    @Override
    public void create(CategoryCreateDTO dto) {

        // Convert DTO to entity
        Category category = new Category();
        BeanUtils.copyProperties(dto, category);

        // Set merchant ID from current user context
        category.setMerchantId(UserContext.getCurrentUser());

        categoryMapper.insert(category);
    }

    /**
     * Query categories with pagination
     *
     * @param query
     */
    @Override
    public PageResult<CategoryDTO> list(CategoryQueryDTO query) {

        Long merchantId = UserContext.getCurrentUser();

        // Calculate offset
        Integer page = query.getPage();
        Integer pageSize = query.getPageSize();
        int offset = (page - 1) * pageSize;

        // Query data
        List<CategoryDTO> records = categoryMapper.findAll(
                query.getType(),
                query.getStatus(),
                merchantId,
                offset,
                pageSize
        );

        // Query total count
        Long total = categoryMapper.count(
                query.getType(),
                query.getStatus(),
                merchantId
        );


        return new PageResult<>(total, records);
    }

    /**
     * Delete category by id
     *
     * @param id
     */
    @Override
    public void deleteById(Long id) {

        // Validate input parameter
        AssertUtil.notNull(id, "Category id cannot be null");

        // TODO MyBatis Interceptor
        Long merchantId = UserContext.getCurrentUser();

        // Check if the category exists and belongs to current merchant
        Category category = categoryMapper.findById(id, merchantId);
        AssertUtil.notNull(category, "Category not found");

        // Check if there are dishes associated with this category
        int count = dishMapper.countByCategoryId(id, merchantId);
        if (count > 0) {
            throw new IllegalArgumentException("Category cannot be deleted because it has dishes");
        }

        // Delete category with merchant scope restriction
        int rows = categoryMapper.deleteById(id, merchantId);
        AssertUtil.checkRows(rows, "Category not found or no permission");

    }

    /**
     * Update category by id
     *
     * @param dto
     */
    @Override
    public void update(CategoryUpdateDTO dto) {

        AssertUtil.notNull(dto.getId(), "Category id cannot be null");

        if (dto.getName() == null &&
                dto.getType() == null &&
                dto.getStatus() == null &&
                dto.getSort() == null) {
            throw new IllegalArgumentException("No fields to update");
        }

        // Convert DTO to entity
        Category category = new Category();
        BeanUtils.copyProperties(dto, category);

        // Set merchant ID from current user context
        category.setMerchantId(UserContext.getCurrentUser());

        int rows = categoryMapper.update(category);
        AssertUtil.checkRows(rows, "Category not found or no permission");

    }

}
