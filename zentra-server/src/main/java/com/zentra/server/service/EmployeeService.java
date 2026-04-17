package com.zentra.server.service;

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
}
