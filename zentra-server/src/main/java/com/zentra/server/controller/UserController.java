package com.zentra.server.controller;

import com.zentra.common.result.PageResult;
import com.zentra.common.result.Result;
import com.zentra.server.annotation.*;
import com.zentra.server.dto.*;
import com.zentra.server.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for user APIs
 */
@Tag(
        name = "User APIs",
        description = "User account and user order APIs"
)
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(
            UserService userService
    ) {

        this.userService = userService;
    }

    /**
     * User registration
     */
    @Operation(
            summary = "User registration",
            description = "Register a new user account"
    )
    @SuccessApiResponse
    @ValidationErrorApiResponse
    @ConflictApiResponse
    @PostMapping("/register")
    public Result<Void> register(
            @Valid @RequestBody UserRegisterDTO dto
    ) {

        userService.register(dto);

        return Result.success();
    }

    /**
     * User login
     */
    @Operation(
            summary = "User login",
            description =
                    "Authenticate user and return JWT token. " +
                    "This API includes login rate limiting, " +
                    "verification code validation, and " +
                    "verification retry protection"
    )
    @LoginApiResponse
    @ValidationErrorApiResponse
    @NotFoundApiResponse
    @TooManyRequestApiResponse
    @PostMapping("/login")
    public Result<LoginResponse> login(
            @Valid @RequestBody UserLoginDTO dto
    ) {

        return Result.success(
                userService.login(dto)
        );
    }

    /**
     * User logout
     */
    @Operation(
            summary = "User logout",
            description =
                    "Logout current user and revoke JWT token. " +
                    "This API adds the current JWT token to the " +
                    "distributed blacklist runtime"
    )
    @SuccessApiResponse
    @AuthApiResponses
    @TokenBlacklistedApiResponse
    @PostMapping("/logout")
    public Result<Void> logout(
            HttpServletRequest request
    ) {

        String authHeader = request.getHeader("Authorization");

        String token = authHeader.substring(7);

        userService.logout(token);

        return Result.success();
    }

    /**
     * Get current user profile
     */
    @Operation(
            summary = "Get current user profile",
            description = "Retrieve profile information of the current authenticated user"
    )
    @UserApiResponse
    @AuthApiResponses
    @GetMapping("/profile")
    public Result<UserDTO> profile() {

        return Result.success(
                userService.getProfile()
        );
    }

    /**
     * Get current user's orders
     */
    @Operation(
            summary = "Get current user's orders",
            description = "Retrieve paginated order list of the current authenticated user"
    )
    @OrderPageApiResponse
    @AuthApiResponses
    @GetMapping("/orders")
    public Result<PageResult<OrderPageDTO>> getMyOrders(
            @Valid OrderQueryDTO query
    ) {

        return Result.success(
                userService.getMyOrders(query)
        );
    }
}
