package com.zentra.server.controller;

import com.zentra.common.constant.PermissionConstants;
import com.zentra.common.result.Result;
import com.zentra.server.annotation.AuthApiResponses;
import com.zentra.server.annotation.AuditLog;
import com.zentra.server.annotation.DashboardApiResponse;
import com.zentra.server.annotation.RequirePermission;
import com.zentra.server.dto.DashboardDTO;
import com.zentra.server.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for dashboard APIs
 */
@Tag(
        name = "Dashboard APIs",
        description = "Dashboard statistics APIs"
)
@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(
            DashboardService dashboardService
    ) {

        this.dashboardService = dashboardService;

    }

    /**
     * Get dashboard statistics
     */
    @Operation(
            summary = "Get dashboard statistics",
            description =
                    "Retrieve dashboard statistics. " +
                            "Requires permission: dashboard:view"
    )
    @DashboardApiResponse
    @AuthApiResponses
    @RequirePermission(
            PermissionConstants.DASHBOARD_VIEW
    )
    @AuditLog(
            operation = "VIEW_DASHBOARD",
            resourceType = "dashboard"
    )
    @GetMapping
    public Result<DashboardDTO> statistics() {

        return Result.success(
                dashboardService.statistics()
        );

    }

}