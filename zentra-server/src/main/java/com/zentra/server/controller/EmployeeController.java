package com.zentra.server.controller;

import com.zentra.common.result.Result;
import com.zentra.server.entity.Employee;
import com.zentra.server.service.EmployeeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    /**
     * Get employee by id
     */
    @GetMapping("/employee/{id}")
    public Result<Employee> getEmployeeById(@PathVariable Long id) {
        Employee employee = employeeService.getById(id);
        return Result.success(employee);
    }

    /**
     * Get all employees
     */
    @GetMapping("/employees")
    public Result<List<Employee>> getAllEmployees() {
        return Result.success(employeeService.getAll());
    }

    /**
     * Create a new employee
     */
    @PostMapping("/employee")
    public Result<String> createEmployee(@RequestBody Employee employee) {
        employeeService.create(employee);
        return Result.success("Employee created successfully");
        }
}
