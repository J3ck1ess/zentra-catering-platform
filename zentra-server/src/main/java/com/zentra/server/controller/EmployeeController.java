package com.zentra.server.controller;

import com.zentra.common.result.PageResult;
import com.zentra.common.result.Result;
import com.zentra.server.context.UserContext;
import com.zentra.server.dto.*;
import com.zentra.server.entity.Employee;
import com.zentra.server.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for Employee APIs
 */
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    /**
     * Create a new employee
     *
     * @param dto
     * @return
     */
    @PostMapping
    public Result<Void> createEmployee(@Valid @RequestBody EmployeeCreateDTO dto) {

        employeeService.create(dto);
        return Result.success();
    }

    /**
     * Get employees with pagination and optional filters
     *
     * @param query
     * @return
     */
    @GetMapping
    public Result<PageResult<EmployeeDTO>> list(@Valid EmployeeQueryDTO query) {

        return Result.success(employeeService.list(query));
    }

    /**
     * Get employee by id
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<EmployeeDTO> getById(@PathVariable Long id) {

        return Result.success(employeeService.getById(id));
    }

    /**
     * Get employee by username
     *
     * @param username
     * @return
     */
    @GetMapping("/search")
    public Result<EmployeeDTO> getByUsername(@RequestParam String username) {

        return Result.success(employeeService.getByUsername(username));
    }

    /**
     * Login
     *
     * @param dto
     * @return
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody EmployeeLoginDTO dto) {

        return Result.success(employeeService.login(dto));
    }

    /**
     * Update employee
     *
     * @param dto
     * @return
     */
    @PatchMapping
    public Result<Void> update(@Valid @RequestBody EmployeeUpdateDTO dto) {

        employeeService.update(dto);
        return Result.success();
    }

    /**
     * Delete employee by id
     *
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {

        employeeService.deleteById(id);
        return Result.success();
    }
}
