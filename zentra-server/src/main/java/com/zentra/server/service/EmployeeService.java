package com.zentra.server.service;

import com.zentra.server.context.UserContext;
import com.zentra.server.dto.LoginResponse;
import com.zentra.server.entity.Employee;

import java.util.List;

public interface EmployeeService {

    /**
     * Get employee by username
     */
    Employee getByUsername(String username);

    /**
     * Get employee by id
     */
    Employee getById(Long id);

    /**
     * Get all employees
     */
    List<Employee> getAll();

    /**
     * Create employee
     */
    void create(Employee employee);

    /**
     * Login
     */
    LoginResponse login(Employee employee);

    /**
     * Get current user id
     */
    Long userId = UserContext.getCurrentUser();
}
