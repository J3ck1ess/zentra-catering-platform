package com.zentra.server.controller;

import com.zentra.common.result.Result;
import com.zentra.server.entity.Employee;
import com.zentra.server.service.EmployeeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for Employee APIs
 */
@RestController
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    /**
     * Get employee by username
     */
    @GetMapping("/employee")
    public Result<Employee> getEmployee(@RequestParam String username) {
        Employee employee = employeeService.getByUsername(username);
        return Result.success(employee);
    }
}
