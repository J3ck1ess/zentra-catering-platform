package com.zentra.server.service.impl;

import com.zentra.common.auth.AuthInfo;
import com.zentra.common.constant.EmployeeStatus;
import com.zentra.common.constant.UserType;
import com.zentra.common.context.AuthContext;
import com.zentra.common.result.PageResult;
import com.zentra.common.util.AssertUtil;
import com.zentra.common.util.PasswordUtil;
import com.zentra.server.dto.*;
import com.zentra.server.entity.Employee;
import com.zentra.server.mapper.EmployeeMapper;
import com.zentra.server.service.EmployeeService;
import com.zentra.common.util.JwtUtil;
import org.springframework.beans.BeanUtils;
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

    /**
     * Create employee
     *
     * @param dto
     */
    @Override
    public void create(EmployeeCreateDTO dto) {

        Employee employee = new Employee();
        BeanUtils.copyProperties(dto, employee);

        // Encrypt password
        employee.setPassword(
                PasswordUtil.encode(dto.getPassword())
        );

        employee.setStatus(EmployeeStatus.ACTIVE);
        employee.setMerchantId(AuthContext.getCurrentMerchantId());

        int rows = employeeMapper.insert(employee);
        AssertUtil.checkRows(rows, "Failed to create employee");
    }

    /**
     * Query employees with pagination
     *
     * @param query
     * @return
     */
    @Override
    public PageResult<EmployeeDTO> list(EmployeeQueryDTO query) {

        Long merchantId = AuthContext.getCurrentMerchantId();

        Integer page = query.getPage();
        Integer pageSize = query.getPageSize();
        int offset = (page - 1) * pageSize;

        List<Employee> list = employeeMapper.findPage(
                query.getUsername(),
                query.getStatus(),
                merchantId,
                offset,
                pageSize
        );

        // Convert Entity -> DTO
        List<EmployeeDTO> records = list.stream().map(employee -> {

            EmployeeDTO dto = new EmployeeDTO();
            BeanUtils.copyProperties(employee, dto);

            return dto;
        }).toList();

        Long total = employeeMapper.count(
                query.getUsername(),
                query.getStatus(),
                merchantId
        );

        return new PageResult<>(total, records);
    }

    /**
     * Get employee by id
     *
     * @param id
     * @return
     */
    @Override
    public EmployeeDTO getById(Long id) {

        Long merchantId = AuthContext.getCurrentMerchantId();

        Employee employee = employeeMapper.findById(id, merchantId);
        AssertUtil.notNull(employee, "Employee not found");

        EmployeeDTO dto = new EmployeeDTO();
        BeanUtils.copyProperties(employee, dto);

        return dto;
    }

    /**
     * Get employee by username
     *
     * @param username
     * @return
     */
    @Override
    public EmployeeDTO getByUsername(String username) {

        Long merchantId = AuthContext.getCurrentMerchantId();

        Employee employee = employeeMapper.findByUsername(username, merchantId);
        AssertUtil.notNull(employee, "Employee not found");

        EmployeeDTO dto = new EmployeeDTO();
        BeanUtils.copyProperties(employee, dto);

        return dto;
    }

    /**
     * Login employee
     *
     * @param dto
     * @return
     */
    @Override
    public LoginResponse login(EmployeeLoginDTO dto) {

        // Query database
        Employee dbEmployee = employeeMapper.findByUsernameOnly(
                dto.getUsername()
        );

        // Check if the user exists
        AssertUtil.notNull(dbEmployee, "User not found");

        // Verify account status
        if (dbEmployee.getStatus().equals(EmployeeStatus.DISABLED)) {
            throw new IllegalArgumentException("Account disabled");
        }

        // Verify password
        if (!PasswordUtil.matches(
                dto.getPassword(),
                dbEmployee.getPassword()
        )) {

            throw new IllegalArgumentException("Incorrect password");
        }

        // Generate JWT token
        AuthInfo authInfo = new AuthInfo(
                dbEmployee.getId(),
                dbEmployee.getMerchantId(),
                UserType.EMPLOYEE
        );

        String token = JwtUtil.generateToken(authInfo);

        return new LoginResponse(token, dbEmployee.getId());
    }

    /**
     * Update employee
     *
     * @param dto
     */
    @Override
    public void update(EmployeeUpdateDTO dto) {

        AssertUtil.notNull(dto.getId(), "Employee id cannot be null");

        if (dto.getUsername() == null
                && dto.getName() == null
                && dto.getRole() == null
                && dto.getStatus() == null) {

            throw new IllegalArgumentException("No fields to update");
        }

        // Convert DTO -> Entity
        Employee employee = new Employee();
        BeanUtils.copyProperties(dto, employee);

        employee.setMerchantId(AuthContext.getCurrentMerchantId());

        // Execute update
        int rows = employeeMapper.update(employee);
        AssertUtil.checkRows(rows, "Employee not found or no permission");
    }

    /**
     * Delete employee by id
     *
     * @param id
     */
    @Override
    public void deleteById(Long id) {

        AssertUtil.notNull(id, "Employee id cannot be null");

        Long merchantId = AuthContext.getCurrentMerchantId();

        // Execute delete
        int rows = employeeMapper.deleteById(id, merchantId);
        AssertUtil.checkRows(rows, "Employee not found or no permission");
    }
}
