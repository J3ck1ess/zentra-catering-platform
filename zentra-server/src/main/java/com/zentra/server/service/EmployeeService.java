package com.zentra.server.service;

import com.zentra.server.entity.Employee;

public interface EmployeeService {

    /**
     * Get employee by username
     */
    Employee getByUsername(String username);
}
