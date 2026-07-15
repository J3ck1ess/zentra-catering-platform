package com.zentra.server.controller;

import com.zentra.common.constant.PermissionConstants;
import com.zentra.common.result.PageResult;
import com.zentra.common.result.Result;
import com.zentra.server.annotation.*;
import com.zentra.server.dto.*;
import com.zentra.server.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for Employee APIs
 */
@Tag(
        name = "Employee APIs",
        description =
                "Employee management APIs with RBAC permission control"
)
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(
            EmployeeService employeeService
    ) {

        this.employeeService = employeeService;
    }

    /**
     * Create a new employee
     */
    @Operation(
            summary = "Create employee",
            description =
                    "Create a new employee account. " +
                    "Requires permission: employee:create"
    )
    @SuccessApiResponse
    @ValidationErrorApiResponse
    @ConflictApiResponse
    @AuthApiResponses
    @RequirePermission(
            PermissionConstants.EMPLOYEE_CREATE
    )
    @AuditLog(
            operation = "CREATE_EMPLOYEE",
            resourceType = "employee"
    )
    @PostMapping
    public Result<Void> createEmployee(
            @Valid @RequestBody EmployeeCreateDTO dto
    ) {

        employeeService.create(dto);

        return Result.success();
    }

    /**
     * Get employees with pagination and optional filters
     */
    @Operation(
            summary = "Get employees list",
            description =
                    "Retrieve paginated employee list with optional filters. " +
                    "Requires permission: employee:view"
    )
    @EmployeePageApiResponse
    @AuthApiResponses
    @RequirePermission(
            PermissionConstants.EMPLOYEE_VIEW
    )
    @AuditLog(
            operation = "PAGE_EMPLOYEE",
            resourceType = "employee"
    )
    @GetMapping
    public Result<PageResult<EmployeeDTO>> page(
            @Valid EmployeeQueryDTO query
    ) {

        return Result.success(
                employeeService.page(query)
        );
    }

    /**
     * Get employee by id
     */
    @Operation(
            summary = "Get employee by id",
            description =
                    "Retrieve employee information by employee id. " +
                    "Requires permission: employee:view"
    )
    @EmployeeApiResponse
    @NotFoundApiResponse
    @AuthApiResponses
    @RequirePermission(
            PermissionConstants.EMPLOYEE_VIEW
    )
    @AuditLog(
            operation = "GET_EMPLOYEE",
            resourceType = "employee"
    )
    @GetMapping("/{id}")
    public Result<EmployeeDTO> getById(
            @PathVariable Long id
    ) {

        return Result.success(
                employeeService.getById(id)
        );
    }

    /**
     * Get employee by username
     */
    @Operation(
            summary = "Get employee by username",
            description =
                    "Retrieve employee information by username. " +
                    "Requires permission: employee:view"
    )
    @EmployeeApiResponse
    @NotFoundApiResponse
    @AuthApiResponses
    @RequirePermission(
            PermissionConstants.EMPLOYEE_VIEW
    )
    @AuditLog(
            operation = "SEARCH_EMPLOYEE",
            resourceType = "employee"
    )
    @GetMapping("/search")
    public Result<EmployeeDTO> getByUsername(
            @RequestParam String username
    ) {

        return Result.success(
                employeeService.getByUsername(username)
        );
    }

    /**
     * Get current login employee
     */
    @Operation(
            summary = "Get current employee",
            description =
                    "Retrieve current login employee information."
    )
    @EmployeeApiResponse
    @AuthApiResponses
    @GetMapping("/me")
    public Result<EmployeeDTO> getCurrentEmployee() {

        return Result.success(
                employeeService.getCurrentEmployee()
        );

    }

    /**
     * Employee login
     */
    @Operation(
            summary = "Employee login",
            description = "Authenticate employee and return JWT token"
    )
    @LoginApiResponse
    @ValidationErrorApiResponse
    @NotFoundApiResponse
    @PostMapping("/login")
    public Result<LoginResponse> login(
            @Valid @RequestBody EmployeeLoginDTO dto
    ) {

        return Result.success(
                employeeService.login(dto)
        );
    }

    /**
     * Update employee
     */
    @Operation(
            summary = "Update employee",
            description =
                    "Update employee information. " +
                    "Requires permission: employee:update"
    )
    @SuccessApiResponse
    @ValidationErrorApiResponse
    @NotFoundApiResponse
    @AuthApiResponses
    @RequirePermission(
            PermissionConstants.EMPLOYEE_UPDATE
    )
    @AuditLog(
            operation = "UPDATE_EMPLOYEE",
            resourceType = "employee"
    )
    @PatchMapping
    public Result<Void> update(
            @Valid @RequestBody EmployeeUpdateDTO dto
    ) {

        employeeService.update(dto);

        return Result.success();
    }

    /**
     * Update employee status
     */
    @Operation(
            summary = "Update employee status",
            description =
                    "Enable or disable an employee account. " +
                    "Requires permission: employee:update"
    )
    @SuccessApiResponse
    @ValidationErrorApiResponse
    @NotFoundApiResponse
    @AuthApiResponses
    @RequirePermission(
            PermissionConstants.EMPLOYEE_UPDATE
    )
    @AuditLog(
            operation = "UPDATE_EMPLOYEE_STATUS",
            resourceType = "employee"
    )
    @PutMapping("/status")
    public Result<Void> updateStatus(
            @Valid @RequestBody EmployeeStatusDTO dto
    ) {

        employeeService.updateStatus(dto);

        return Result.success();
    }

    /**
     * Delete employee by id
     */
    @Operation(
            summary = "Delete employee",
            description =
                    "Delete an employee account by id. " +
                    "Requires permission: employee:delete"
    )
    @SuccessApiResponse
    @NotFoundApiResponse
    @AuthApiResponses
    @RequirePermission(
            PermissionConstants.EMPLOYEE_DELETE
    )
    @AuditLog(
            operation = "DELETE_EMPLOYEE",
            resourceType = "employee"
    )
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @PathVariable Long id
    ) {

        employeeService.deleteById(id);

        return Result.success();
    }
}
