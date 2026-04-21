package com.zentra.server.service.impl;

import com.zentra.server.dto.LoginResponse;
import com.zentra.server.entity.Employee;
import com.zentra.server.mapper.EmployeeMapper;
import com.zentra.server.service.EmployeeService;
import com.zentra.server.utils.JwtUtil;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service implementation for Employee
 */
@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeMapper employeeMapper;

    public EmployeeServiceImpl(EmployeeMapper employeeMapper) {
        this.employeeMapper = employeeMapper;
    }

    @Override
    public Employee getByUsername(String username) {
        return employeeMapper.findByUsername(username);
    }

    @Override
    public Employee getById(Long id) {
        Employee employee = employeeMapper.findById(id);
        if (employee == null) {
            throw new RuntimeException("employee not found");
        }
        return employee;
    }

    @Override
    public List<Employee> getAll() {
        return employeeMapper.findAll();
    }

    @Override
    public void create(Employee employee) {

        // Set default values
        employee.setStatus(1);
        employee.setMerchantId(1L);

        employeeMapper.insert(employee);
    }

    @Override
    public LoginResponse login(Employee employee) {

        // Query database
        Employee dbEmployee = employeeMapper.findByUsername(employee.getUsername());

        // Check if the username exists
        if (dbEmployee == null) {
            throw new RuntimeException("User not found");
        }

        // Check if the password is correct
        if (!dbEmployee.getPassword().equals(employee.getPassword())) {
            throw new RuntimeException("Incorrect password");
        }

        // Verification status
        if (dbEmployee.getStatus() == 0) {
            throw new RuntimeException("Account disabled");
        }

        // Generate token
        String token = JwtUtil.generateToken(dbEmployee.getId());

        // Return DTO
        return new LoginResponse(token, dbEmployee.getId());
    }
}
