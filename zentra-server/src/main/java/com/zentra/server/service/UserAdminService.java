package com.zentra.server.service;

import com.zentra.common.result.PageResult;
import com.zentra.server.dto.UserAdminDTO;
import com.zentra.server.dto.UserAdminQueryDTO;

/**
 * Service Interface for admin user management
 */
public interface UserAdminService {

    /**
     * Query users with pagination
     */
    PageResult<UserAdminDTO> page(UserAdminQueryDTO query);

    /**
     * Update user status
     */
    void updateStatus(Long userId, Integer status);
}
