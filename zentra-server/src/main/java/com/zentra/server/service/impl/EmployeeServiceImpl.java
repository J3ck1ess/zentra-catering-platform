package com.zentra.server.service.impl;

import com.zentra.common.auth.AuthInfo;
import com.zentra.common.constant.*;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service implementation for Employee
 */
@Service
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeMapper employeeMapper;

    public EmployeeServiceImpl(EmployeeMapper employeeMapper) {
        this.employeeMapper = employeeMapper;
    }

    /**
     * Create employee
     */
    @Override
    public void create(EmployeeCreateDTO dto) {

        // Validate employee role
        AssertUtil.isTrue(
                RoleConstants.isValid(dto.getRole()),
                ErrorCode.INVALID_EMPLOYEE_ROLE,
                ErrorMessage.INVALID_EMPLOYEE_ROLE
        );

        Long merchantId = AuthContext.getCurrentMerchantId();
        log.info(
                "[EMPLOYEE] Employee creation started. merchantId={}, username={}, role={}",
                merchantId,
                dto.getUsername(),
                dto.getRole()
        );

        // Check username duplication
        Employee existEmployee = employeeMapper.findByUsername(
                dto.getUsername(),
                merchantId
        );

        if (existEmployee != null) {
            log.warn(
                    "[EMPLOYEE] Duplicate employee username detected. merchantId={}, username={}",
                    merchantId,
                    dto.getUsername()
            );
        }

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

        try {

            int rows = employeeMapper.insert(employee);

            AssertUtil.checkRows(
                    rows,
                    ErrorCode.EMPLOYEE_CREATE_FAILED,
                    ErrorMessage.EMPLOYEE_CREATE_FAILED
            );

        } catch (DuplicateKeyException e) {

            log.warn(
                    "[EMPLOYEE] Duplicate employee username detected. merchantId={}, username={}",
                    merchantId,
                    employee.getUsername()
            );

            throw new BusinessException(
                    ErrorCode.EMPLOYEE_USERNAME_ALREADY_EXISTS,
                    ErrorMessage.EMPLOYEE_USERNAME_ALREADY_EXISTS
            );
        }

        log.info(
                "[EMPLOYEE] Employee created successfully. merchantId={}, username={}, role={}",
                merchantId,
                employee.getUsername(),
                employee.getRole()
        );
    }

    /**
     * Query employees with pagination
     */
    @Override
    public PageResult<EmployeeDTO> page(EmployeeQueryDTO query) {

        Long merchantId = AuthContext.getCurrentMerchantId();

        Integer page = query.getPage();
        Integer pageSize = query.getPageSize();
        int offset = (page - 1) * pageSize;
        log.info(
                "[EMPLOYEE] Employee page query started. merchantId={}, page={}, pageSize={}, username={}, status={}",
                merchantId,
                page,
                pageSize,
                query.getUsername(),
                query.getStatus()
        );

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

        log.info(
                "[EMPLOYEE] Employee page query completed. merchantId={}, total={}",
                merchantId,
                total
        );

        return new PageResult<>(total, records);
    }

    /**
     * Get employee by id
     */
    @Override
    public EmployeeDTO getById(Long id) {

        Long merchantId = AuthContext.getCurrentMerchantId();
        log.info(
                "[EMPLOYEE] Employee detail query started. merchantId={}, employeeId={}",
                merchantId,
                id
        );

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

        log.info(
                "[EMPLOYEE] Employee detail query completed. merchantId={}, employeeId={}",
                merchantId,
                id
        );
        return dto;
    }

    /**
     * Get employee by username
     */
    @Override
    public EmployeeDTO getByUsername(String username) {

        Long merchantId = AuthContext.getCurrentMerchantId();
        log.info(
                "[EMPLOYEE] Employee username query started. merchantId={}, username={}",
                merchantId,
                username
        );

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

        log.info(
                "[EMPLOYEE] Employee username query completed. merchantId={}, username={}",
                merchantId,
                username
        );
        return dto;
    }

    /**
     * Login employee
     */
    @Override
    public LoginResponse login(EmployeeLoginDTO dto) {

        // Query database
        log.info(
                "[AUTH] Employee login started. username={}",
                dto.getUsername()
        );
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

            log.warn(
                    "[AUTH] Disabled employee attempted login. employeeId={}, username={}",
                    dbEmployee.getId(),
                    dbEmployee.getUsername()
            );

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

            log.warn(
                    "[AUTH] Employee password mismatch. employeeId={}, username={}",
                    dbEmployee.getId(),
                    dbEmployee.getUsername()
            );

            throw new BusinessException(
                    ErrorCode.EMPLOYEE_USERNAME_OR_PASSWORD_ERROR,
                    ErrorMessage.EMPLOYEE_USERNAME_OR_PASSWORD_ERROR
            );
        }

        // Generate JWT token
        AuthInfo authInfo = new AuthInfo(
                dbEmployee.getId(),
                dbEmployee.getMerchantId(),
                UserType.EMPLOYEE,
                dbEmployee.getRole()
        );

        String token = JwtUtil.generateToken(authInfo);

        log.info(
                "[AUTH] Employee login successful. employeeId={}, merchantId={}, role={}",
                dbEmployee.getId(),
                dbEmployee.getMerchantId(),
                dbEmployee.getRole()
        );
        return new LoginResponse(token, dbEmployee.getId());
    }

    /**
     * Update employee
     */
    @Override
    public void update(EmployeeUpdateDTO dto) {

        // Validate employee role
        if (dto.getRole() != null) {

            AssertUtil.isTrue(
                    RoleConstants.isValid(dto.getRole()),
                    ErrorCode.INVALID_EMPLOYEE_ROLE,
                    ErrorMessage.INVALID_EMPLOYEE_ROLE
            );
        }


        if (dto.getUsername() == null
                && dto.getName() == null
                && dto.getRole() == null) {

            throw new BusinessException(
                    ErrorCode.EMPLOYEE_UPDATE_FAILED,
                    ErrorMessage.EMPLOYEE_UPDATE_FAILED
            );
        }

        Long merchantId = AuthContext.getCurrentMerchantId();
        log.info(
                "[EMPLOYEE] Employee update started. merchantId={}, employeeId={}",
                merchantId,
                dto.getId()
        );

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

        log.info(
                "[EMPLOYEE] Employee update successfully. merchantId={}, employeeId={}",
                merchantId,
                dto.getId()
        );
    }

    /**
     * Update employee status
     */
    @Override
    public void updateStatus(EmployeeStatusDTO dto) {

        Long merchantId = AuthContext.getCurrentMerchantId();

        log.info(
                "[EMPLOYEE] Employee status update started. merchantId={}, employeeId={}, status={}",
                merchantId,
                dto.getId(),
                dto.getStatus()
        );

        // Query employee
        Employee employee = employeeMapper.findById(
                dto.getId(),
                merchantId
        );

        // Check employee existence
        AssertUtil.notNull(
                employee,
                ErrorCode.EMPLOYEE_NOT_FOUND,
                ErrorMessage.EMPLOYEE_NOT_FOUND
        );

        // Execute status update
        int rows = employeeMapper.updateStatus(
                dto.getId(),
                merchantId,
                dto.getStatus()
        );

        AssertUtil.checkRows(
                rows,
                ErrorCode.EMPLOYEE_UPDATE_FAILED,
                ErrorMessage.EMPLOYEE_UPDATE_FAILED
        );

        log.info(
                "[EMPLOYEE] Employee status updated successfully. merchantId={}, employeeId={}, status={}",
                merchantId,
                dto.getId(),
                dto.getStatus()
        );
    }

    /**
     * Delete employee by id
     */
    @Override
    public void deleteById(Long id) {

        Long merchantId = AuthContext.getCurrentMerchantId();
        log.info(
                "[EMPLOYEE] Employee delete started. merchantId={}, employeeId={}",
                merchantId,
                id
        );

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

        // Prevent current employee from deleting themselves
        Long currentUserId = AuthContext.getCurrentUserId();

        AssertUtil.isTrue(
                !id.equals(currentUserId),
                ErrorCode.EMPLOYEE_SELF_DELETE_NOT_ALLOWED,
                ErrorMessage.EMPLOYEE_SELF_DELETE_NOT_ALLOWED
        );

        // Prevent deleting the last SUPER_ADMIN
        if (RoleConstants.SUPER_ADMIN.equals(employee.getRole())) {

            Long superAdminCount = employeeMapper.countByRole(
                    merchantId,
                    RoleConstants.SUPER_ADMIN
            );

            AssertUtil.isTrue(
                    superAdminCount > 1,
                    ErrorCode.LAST_SUPER_ADMIN_DELETE_NOT_ALLOWED,
                    ErrorMessage.LAST_SUPER_ADMIN_DELETE_NOT_ALLOWED
            );

        }

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

        log.info(
                "[EMPLOYEE] Employee deleted successfully. merchantId={}, employeeId={}, username={}, role={}",
                merchantId,
                id,
                employee.getUsername(),
                employee.getRole()
        );
    }
}
