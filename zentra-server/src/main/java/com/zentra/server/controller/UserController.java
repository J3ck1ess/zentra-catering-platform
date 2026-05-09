package com.zentra.server.controller;

import com.zentra.common.result.PageResult;
import com.zentra.common.result.Result;
import com.zentra.server.dto.*;
import com.zentra.server.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for user APIs
 */
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * User registration
     */
    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody UserRegisterDTO dto) {

        userService.register(dto);
        return Result.success();
    }

    /**
     * User login
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody UserLoginDTO dto) {

        return Result.success(userService.login(dto));
    }

    /**
     * Get current user profile
     */
    @GetMapping("/profile")
    public Result<UserDTO> profile() {

        return Result.success(userService.getProfile());
    }

    /**
     * Get current user's orders
     */
    @GetMapping("/orders")
    public Result<PageResult<OrderPageDTO>> getMyOrders(@Valid OrderQueryDTO query) {

        return Result.success(userService.getMyOrders(query));
    }
}
