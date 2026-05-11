package com.zentra.server.service.impl;

import com.zentra.common.auth.AuthInfo;
import com.zentra.common.constant.EmployeeStatus;
import com.zentra.common.constant.ErrorCode;
import com.zentra.common.constant.ErrorMessage;
import com.zentra.common.constant.UserType;
import com.zentra.common.context.AuthContext;
import com.zentra.common.exception.BusinessException;
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

        Long merchantId = AuthContext.getCurrentMerchantId();

        // Check username duplication
        Employee existEmployee = employeeMapper.findByUsername(
                dto.getUsername(),
                merchantId
        );

        AssertUtil.isNull(
                existEmployee,
                ErrorCode.EMPLOYEE_USERNAME_ALREADY_EXISTS,
                ErrorMessage.EMPLOYEE_USERNAME_ALREADY_EXISTS
        );

        // Convert DTO -> Entity
        Employee employee = new Employee();
        BeanUtils.copyProperties(dto, employee);

        // Encrypt password
        employee.setPassword(
                PasswordUtil.encode(dto.getPassword())
        );

        // Set default properties
        employee.setStatus(EmployeeStatus.ACTIVE);
        employee.setMerchantId(merchantId);

        int rows = employeeMapper.insert(employee);
        AssertUtil.checkRows(
                rows,
                ErrorCode.EMPLOYEE_CREATE_FAILED,
                ErrorMessage.EMPLOYEE_CREATE_FAILED
        );
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

        // Query employee
        Employee employee = employeeMapper.findById(
                id,
                merchantId
        );

        // Check employee existence
        AssertUtil.notNull(
                employee,
                ErrorCode.EMPLOYEE_NOT_FOUND,
                ErrorMessage.EMPLOYEE_NOT_FOUND
        );

        // Convert Entity -> DTO
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

        // Query employee
        Employee employee = employeeMapper.findByUsername(
                username,
                merchantId
        );

        // Check employee existence
        AssertUtil.notNull(
                employee,
                ErrorCode.EMPLOYEE_NOT_FOUND,
                ErrorMessage.EMPLOYEE_NOT_FOUND
        );

        // Convert Entity -> DTO
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

        // Check employee existence
        AssertUtil.notNull(
                dbEmployee,
                ErrorCode.EMPLOYEE_USERNAME_OR_PASSWORD_ERROR,
                ErrorMessage.EMPLOYEE_USERNAME_OR_PASSWORD_ERROR
        );

        // Verify account status
        if (dbEmployee.getStatus().equals(EmployeeStatus.DISABLED)) {

            throw new BusinessException(
                    ErrorCode.EMPLOYEE_DISABLED,
                    ErrorMessage.EMPLOYEE_DISABLED
            );
        }

        // Verify password
        if (!PasswordUtil.matches(
                dto.getPassword(),
                dbEmployee.getPassword()
        )) {

            throw new BusinessException(
                    ErrorCode.EMPLOYEE_USERNAME_OR_PASSWORD_ERROR,
                    ErrorMessage.EMPLOYEE_USERNAME_OR_PASSWORD_ERROR
            );
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

        if (dto.getUsername() == null
                && dto.getName() == null
                && dto.getRole() == null
                && dto.getStatus() == null) {

            throw new BusinessException(
                    ErrorCode.EMPLOYEE_UPDATE_FAILED,
                    ErrorMessage.EMPLOYEE_UPDATE_FAILED
            );
        }

        Long merchantId = AuthContext.getCurrentMerchantId();

        // Query employee
        Employee dbEmployee = employeeMapper.findById(
                dto.getId(),
                merchantId
        );

        // Check employee existence
        AssertUtil.notNull(
                dbEmployee,
                ErrorCode.EMPLOYEE_NOT_FOUND,
                ErrorMessage.EMPLOYEE_NOT_FOUND
        );

        // Validate employee status
        if (dto.getStatus() != null
                && dbEmployee.getStatus().equals(EmployeeStatus.ACTIVE)
                && dbEmployee.getStatus().equals(EmployeeStatus.DISABLED)) {

            throw new BusinessException(
                    ErrorCode.EMPLOYEE_STATUS_INVALID,
                    ErrorMessage.EMPLOYEE_STATUS_INVALID
            );
        }

        // Convert DTO -> Entity
        Employee employee = new Employee();
        BeanUtils.copyProperties(dto, employee);

        employee.setMerchantId(merchantId);

        // Execute update
        int rows = employeeMapper.update(employee);
        AssertUtil.checkRows(
                rows,
                ErrorCode.EMPLOYEE_UPDATE_FAILED,
                ErrorMessage.EMPLOYEE_UPDATE_FAILED
        );
    }

    /**
     * Delete employee by id
     *
     * @param id
     */
    @Override
    public void deleteById(Long id) {

        Long merchantId = AuthContext.getCurrentMerchantId();

        // Query employee
        Employee employee = employeeMapper.findById(
                id,
                merchantId
        );

        // Check employee existence
        AssertUtil.notNull(
                employee,
                ErrorCode.EMPLOYEE_NOT_FOUND,
                ErrorMessage.EMPLOYEE_NOT_FOUND
        );

        // Execute delete
        int rows = employeeMapper.deleteById(
                id,
                merchantId
        );

        AssertUtil.checkRows(
                rows,
                ErrorCode.EMPLOYEE_DELETE_FAILED,
                ErrorMessage.EMPLOYEE_DELETE_FAILED
        );
    }
}
