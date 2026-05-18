package com.zentra.server.controller;

import com.zentra.common.result.PageResult;
import com.zentra.common.result.Result;
import com.zentra.server.annotation.*;
import com.zentra.server.dto.UserAdminDTO;
import com.zentra.server.dto.UserAdminQueryDTO;
import com.zentra.server.dto.UserStatusUpdateDTO;
import com.zentra.server.service.UserAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for admin user management APIs
 */
@Tag(
        name = "Admin user APIs",
        description = "Admin user management APIs"
)
@RestController
@RequestMapping("/admin/users")
public class UserAdminController {

    private final UserAdminService userAdminService;

    public UserAdminController(
            UserAdminService userAdminService
    ) {

        this.userAdminService = userAdminService;
    }

    /**
     * Get users with pagination and optional filters
     */
    @Operation(
            summary = "Get users list",
            description = "Retrieve paginated user list with optional filters"
    )
    @UserAdminPageApiResponse
    @AuthApiResponses
    @GetMapping
    public Result<PageResult<UserAdminDTO>> page(
            @Valid UserAdminQueryDTO query
    ) {

        return Result.success(
                userAdminService.page(query)
        );
    }

    /**
     * Update user status
     */
    @Operation(
            summary = "Update user status",
            description = "Update user account status"
    )
    @SuccessApiResponse
    @ValidationErrorApiResponse
    @NotFoundApiResponse
    @AuthApiResponses
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UserStatusUpdateDTO dto
    ) {

        userAdminService.updateStatus(
                id,
                dto.getStatus()
        );

        return Result.success();
    }
}
