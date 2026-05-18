package com.zentra.server.service;

import com.zentra.common.result.PageResult;
import com.zentra.server.dto.*;

/**
 * Service interface for employee logic
 */
public interface EmployeeService {

    /**
     * Create employee
     */
    void create(EmployeeCreateDTO dto);

    /**
     * Query employees with pagination
     */
    PageResult<EmployeeDTO> page(EmployeeQueryDTO query);

    /**
     * Get employee by id
     */
    EmployeeDTO getById(Long id);

    /**
     * Get employee by username
     */
    EmployeeDTO getByUsername(String username);

    /**
     * Login
     */
    LoginResponse login(EmployeeLoginDTO dto);

    /**
     * Update employee
     */
    void update(EmployeeUpdateDTO dto);

    /**
     * Delete employee by id
     */
    void deleteById(Long id);
}
