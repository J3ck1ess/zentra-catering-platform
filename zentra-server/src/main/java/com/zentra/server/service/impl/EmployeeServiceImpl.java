package com.zentra.server.service.impl;

import com.zentra.server.entity.Employee;
import com.zentra.server.mapper.EmployeeMapper;
import com.zentra.server.service.EmployeeService;
import org.springframework.stereotype.Service;

/**
 * Service implementation for Employee
 */
@Service
public class EmployeeServiceImpl implements EmployeeService {

    public final EmployeeMapper employeeMapper;

    public EmployeeServiceImpl(EmployeeMapper employeeMapper) {
        this.employeeMapper = employeeMapper;
    }

    @Override
    public Employee getByUsername(String username) {
        return employeeMapper.findByUsername(username);
    }
}
