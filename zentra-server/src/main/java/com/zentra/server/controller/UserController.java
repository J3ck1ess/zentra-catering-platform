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
     * Get user detail
     */
    @Operation(
            summary = "Get user detail",
            description =
                    "Query user detail by user ID with " +
                    "cache penetration protection runtime"
    )
    @SuccessApiResponse
    @GetMapping("/{id}")
    public Result<UserDTO> getUserById(
            @PathVariable Long id
    ) {

        return Result.success(
                userService.getUserById(id)
        );
    }

    /**
     * Update current user profile
     */
    @Operation(
            summary = "Update current user profile",
            description =
                    "Update profile information of the " +
                    "current authenticated user and evict " +
                    "Redis hot data cache"
    )
    @SuccessApiResponse
    @ValidationErrorApiResponse
    @AuthApiResponses
    @PutMapping("/profile")
    public Result<Void> updateProfile(
            @Valid @RequestBody UserUpdateDTO dto
    ) {

        userService.updateProfile(dto);

        return Result.success();
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

    /**
     * Cancel current user's order
     */
    @Operation(
            summary = "Cancel current user's order",
            description =
                    "Cancel a pending order of the current authenticated user"
    )
    @SuccessApiResponse
    @AuthApiResponses
    @NotFoundApiResponse
    @PostMapping("/orders/{id}/cancel")
    public Result<Void> cancelOrder(
            @PathVariable Long id
    ) {

        userService.cancelOrder(id);

        return Result.success();
    }

    /**
     * Simulate order payment
     */
    @Operation(
            summary = "Pay current user's order",
            description =
                    "Simulate payment for a pending order of the current authenticated user"
    )
    @SuccessApiResponse
    @AuthApiResponses
    @NotFoundApiResponse
    @PostMapping("/orders/{id}/pay")
    public Result<Void> payOrder(
            @PathVariable Long id
    ) {

        userService.payOrder(id);

        return Result.success();
    }
}
