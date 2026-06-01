package com.zentra.server.service.impl;

import com.zentra.common.constant.ErrorCode;
import com.zentra.common.constant.ErrorMessage;
import com.zentra.common.constant.UserStatus;
import com.zentra.common.exception.BusinessException;
import com.zentra.common.result.PageResult;
import com.zentra.common.util.AssertUtil;
import com.zentra.server.dto.UserAdminDTO;
import com.zentra.server.dto.UserAdminQueryDTO;
import com.zentra.server.entity.User;
import com.zentra.server.mapper.UserMapper;
import com.zentra.server.service.UserAdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service implementation for admin user management
 */
@Service
@Slf4j
public class UserAdminServiceImpl implements UserAdminService {

    private final UserMapper userMapper;

    public UserAdminServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    /**
     * Query users with pagination
     */
    @Override
    public PageResult<UserAdminDTO> page(UserAdminQueryDTO query) {

        Integer page = query.getPage();
        Integer pageSize = query.getPageSize();
        int offset = (page - 1) * pageSize;

        log.info(
                "[USER_ADMIN] User page query started. page={}, pageSize={}, username={}, status={}",
                page,
                pageSize,
                query.getUsername(),
                query.getStatus()
        );

        List<User> list = userMapper.findPage(
                query.getUsername(),
                query.getStatus(),
                offset,
                pageSize
        );

        // Convert Entity -> DTO
        List<UserAdminDTO> records = list.stream().map(user -> {
            UserAdminDTO dto = new UserAdminDTO();
            BeanUtils.copyProperties(user, dto);

            return dto;
        }).toList();

        Long total = userMapper.count(
                query.getUsername(),
                query.getStatus()
        );

        log.info(
                "[USER_ADMIN] User page query completed. total={}",
                total
        );

        return new PageResult<>(total, records);
    }

    /**
     * Update user status
     */
    @Override
    public void updateStatus(Long userId, Integer status) {

        log.info(
                "[USER_ADMIN] User status update started. userId={}, status={}",
                userId,
                status
        );

        // Validate new status
        if (!UserStatus.isValid(status)) {

            log.warn(
                    "[USER_ADMIN] Invalid user status detected. userId={}, status={}",
                    userId,
                    status
            );

            throw new BusinessException(
                    ErrorCode.USER_STATUS_INVALID,
                    ErrorMessage.USER_STATUS_INVALID
            );
        }

        User user = userMapper.findByIdOnly(userId);

        if (user == null) {
            log.warn(
                    "[USER_ADMIN] User not found during status update. userId={}",
                    userId
            );
        }

        AssertUtil.notNull(user, ErrorCode.USER_NOT_FOUND, ErrorMessage.USER_NOT_FOUND);

        user.setStatus(status);

        // Execute update
        int rows = userMapper.update(user);
        AssertUtil.checkRows(
                rows,
                ErrorCode.USER_STATUS_UPDATE_FAILED,
                ErrorMessage.USER_STATUS_UPDATE_FAILED
        );

        log.info(
                "[USER_ADMIN] User status updated successfully. userId={}, status={}",
                userId,
                status
        );

    }
}
