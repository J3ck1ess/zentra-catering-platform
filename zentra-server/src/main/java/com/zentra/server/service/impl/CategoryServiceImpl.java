package com.zentra.server.service.impl;

import com.zentra.common.constant.CategoryStatus;
import com.zentra.common.constant.CategoryType;
import com.zentra.common.constant.ErrorCode;
import com.zentra.common.constant.ErrorMessage;
import com.zentra.common.context.AuthContext;
import com.zentra.common.exception.BusinessException;
import com.zentra.common.result.PageResult;
import com.zentra.common.util.AssertUtil;
import com.zentra.server.dto.CategoryCreateDTO;
import com.zentra.server.dto.CategoryDTO;
import com.zentra.server.dto.CategoryQueryDTO;
import com.zentra.server.dto.CategoryUpdateDTO;
import com.zentra.server.entity.Category;
import com.zentra.server.mapper.CategoryMapper;
import com.zentra.server.mapper.DishMapper;
import com.zentra.server.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * Implementation of CategoryService
 */
@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;

    private final DishMapper dishMapper;

    public CategoryServiceImpl(CategoryMapper categoryMapper, DishMapper dishMapper) {
        this.categoryMapper = categoryMapper;
        this.dishMapper = dishMapper;
    }

    /**
     * Create a new category
     */
    @Override
    public void create(CategoryCreateDTO dto) {

        // Validate category type
        if (!Objects.equals(dto.getType(), CategoryType.DISH)
                && !Objects.equals(dto.getType(), CategoryType.SET_MEAL)) {

            throw new BusinessException(
                    ErrorCode.CATEGORY_TYPE_INVALID,
                    ErrorMessage.CATEGORY_TYPE_INVALID
            );
        }

        Long merchantId = AuthContext.getCurrentMerchantId();

        log.info(
                "[CATEGORY] Category creation started. merchantId={}, name={}, type={}",
                merchantId,
                dto.getName(),
                dto.getType()
        );

        // Convert DTO -> Entity
        Category category = new Category();
        BeanUtils.copyProperties(dto, category);

        // Default category status
        category.setStatus(CategoryStatus.ENABLED);

        category.setMerchantId(merchantId);

        int rows = categoryMapper.insert(category);
        AssertUtil.checkRows(
                rows,
                ErrorCode.CATEGORY_CREATE_FAILED,
                ErrorMessage.CATEGORY_CREATE_FAILED
        );

        log.info(
                "[CATEGORY] Category created successfully. merchantId={}, categoryName={}",
                category.getMerchantId(),
                category.getName()
        );
    }

    /**
     * Query categories with pagination
     */
    @Override
    public PageResult<CategoryDTO> page(CategoryQueryDTO query) {

        Long merchantId = AuthContext.getCurrentMerchantId();

        // Calculate offset
        Integer page = query.getPage();
        Integer pageSize = query.getPageSize();
        int offset = (page - 1) * pageSize;

        log.info(
                "[CATEGORY] Category page query started. merchantId={}, page={}, pageSize={}, type={}, status={}",
                merchantId,
                page,
                pageSize,
                query.getType(),
                query.getStatus()
        );

        // Query data
        List<Category> list = categoryMapper.findPage(
                query.getType(),
                query.getStatus(),
                merchantId,
                offset,
                pageSize
        );

        // Convert Entity -> DTO
        List<CategoryDTO> records = list.stream().map(category -> {

            CategoryDTO dto = new CategoryDTO();
            BeanUtils.copyProperties(category, dto);

            return dto;
        }).toList();

        // Query total count
        Long total = categoryMapper.count(
                query.getType(),
                query.getStatus(),
                merchantId
        );

        log.info(
                "[CATEGORY] Category page query completed. merchantId={}, total={}",
                merchantId,
                total
        );

        return new PageResult<>(total, records);
    }

    /**
     * Get enabled category list for current merchant
     */
    @Override
    public List<CategoryDTO> list() {

        Long merchantId = AuthContext.getCurrentMerchantId();

        log.info(
                "[CATEGORY] Category list query started. merchantId={}",
                merchantId
        );

        List<Category> categories =
                categoryMapper.findEnabledCategories(
                        merchantId,
                        CategoryStatus.ENABLED
                );

        List<CategoryDTO> result = categories.stream().map(category -> {

            CategoryDTO dto = new CategoryDTO();
            BeanUtils.copyProperties(category, dto);

            return dto;

        }).toList();

        log.info(
                "[CATEGORY] Category list query completed. merchantId={}, categoryCount={}",
                merchantId,
                result.size()
        );

        return result;
    }

    /**
     * Update category
     */
    @Override
    public void update(CategoryUpdateDTO dto) {

        // Check if update fields are empty
        if (dto.getName() == null
                && dto.getType() == null
                && dto.getStatus() == null
                && dto.getSort() == null) {

            throw new BusinessException(
                    ErrorCode.CATEGORY_UPDATE_FAILED,
                    ErrorMessage.CATEGORY_UPDATE_FAILED
            );
        }

        // Validate category type
        if (dto.getType() != null
                && !Objects.equals(dto.getType(), CategoryType.DISH)
                && !Objects.equals(dto.getType(), CategoryType.SET_MEAL)) {

            throw new BusinessException(
                    ErrorCode.CATEGORY_TYPE_INVALID,
                    ErrorMessage.CATEGORY_TYPE_INVALID
            );
        }

        // Validate category status
        if (dto.getStatus() != null
                && !Objects.equals(dto.getStatus(), CategoryStatus.ENABLED)
                && !Objects.equals(dto.getStatus(), CategoryStatus.DISABLED)) {

            throw new BusinessException(
                    ErrorCode.CATEGORY_STATUS_INVALID,
                    ErrorMessage.CATEGORY_STATUS_INVALID
            );
        }

        Long merchantId = AuthContext.getCurrentMerchantId();

        log.info(
                "[CATEGORY] Category update started. merchantId={}, categoryId={}",
                merchantId,
                dto.getId()
        );

        // Query category
        Category dbCategory = categoryMapper.findById(dto.getId(), merchantId);

        if (dbCategory == null) {

            log.warn(
                    "[CATEGORY] Category not found during update. merchantId={}, categoryId={}",
                    merchantId,
                    dto.getId()
            );
        }

        // Check category existence
        AssertUtil.notNull(
                dbCategory,
                ErrorCode.CATEGORY_NOT_FOUND,
                ErrorMessage.CATEGORY_NOT_FOUND
        );

        // Convert DTO to Entity
        Category category = new Category();
        BeanUtils.copyProperties(dto, category);

        // Set merchant ID from current user context
        category.setMerchantId(merchantId);

        int rows = categoryMapper.update(category);
        AssertUtil.checkRows(
                rows,
                ErrorCode.CATEGORY_UPDATE_FAILED,
                ErrorMessage.CATEGORY_UPDATE_FAILED
        );

        log.info(
                "[CATEGORY] Category updated successfully. merchantId={}, categoryId={}",
                merchantId,
                dto.getId()
        );

    }

    /**
     * Delete category by id
     */
    @Override
    public void deleteById(Long id) {

        // TODO MyBatis Interceptor
        Long merchantId = AuthContext.getCurrentMerchantId();

        log.info(
                "[CATEGORY] Category deletion started. merchantId={}, categoryId={}",
                merchantId,
                id
        );

        // Query category
        Category category = categoryMapper.findById(id, merchantId);

        // Check category existence
        if (category == null) {
            log.warn(
                    "[CATEGORY] Category not found during deletion. merchantId={}, categoryId={}",
                    merchantId,
                    id
            );
        }

        AssertUtil.notNull(
                category,
                ErrorCode.CATEGORY_NOT_FOUND,
                ErrorMessage.CATEGORY_NOT_FOUND
        );

        // Check whether category contains dishes
        int count = dishMapper.countByCategoryId(id, merchantId);
        if (count > 0) {

            log.warn(
                    "[CATEGORY] Category deletion rejected. category contains dishes. merchantId={}, categoryId={}, dishCount={}",
                    merchantId,
                    id,
                    count
            );

            throw new BusinessException(
                    ErrorCode.CATEGORY_HAS_DISHES,
                    ErrorMessage.CATEGORY_HAS_DISHES
            );
        }

        // Delete category
        int rows = categoryMapper.deleteById(id, merchantId);
        AssertUtil.checkRows(
                rows,
                ErrorCode.CATEGORY_DELETE_FAILED,
                ErrorMessage.CATEGORY_DELETE_FAILED
        );

        log.info(
                "[CATEGORY] Category deleted successfully. merchantId={}, categoryId={}",
                merchantId,
                id
        );
    }
}
