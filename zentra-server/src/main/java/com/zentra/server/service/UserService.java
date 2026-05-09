package com.zentra.server.service;

import com.zentra.common.result.PageResult;
import com.zentra.server.dto.*;

public interface UserService {

    /**
     * Register user
     */
    void register(UserRegisterDTO dto);

    /**
     * User login
     */
    LoginResponse login(UserLoginDTO dto);

    /**
     * Get current user profile
     * @return
     */
    UserDTO getProfile();

    /**
     * Get current user's orders
     */
    PageResult<OrderPageDTO> getMyOrders(OrderQueryDTO query);
}
