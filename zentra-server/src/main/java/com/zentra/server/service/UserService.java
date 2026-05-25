package com.zentra.server.service;

import com.zentra.common.result.PageResult;
import com.zentra.server.dto.*;
import jakarta.validation.Valid;

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
     * User logout
     */
    void logout(String token);

    /**
     * Get current user profile
     */
    UserDTO getProfile();

    /**
     * Get user detail by ID
     */
    UserDTO getUserById(Long id);

    /**
     * Update current user profile
     */
    void updateProfile(UserUpdateDTO dto);

    /**
     * Get current user's orders
     */
    PageResult<OrderPageDTO> getMyOrders(OrderQueryDTO query);


}
