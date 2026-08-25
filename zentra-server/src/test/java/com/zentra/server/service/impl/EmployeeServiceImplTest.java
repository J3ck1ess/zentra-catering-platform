package com.zentra.server.service.impl;

import com.zentra.common.auth.AuthInfo;
import com.zentra.common.constant.ErrorCode;
import com.zentra.common.constant.ErrorMessage;
import com.zentra.common.constant.EmployeeStatus;
import com.zentra.common.constant.RoleConstants;
import com.zentra.common.context.AuthContext;
import com.zentra.common.exception.BusinessException;
import com.zentra.common.result.PageResult;
import com.zentra.common.util.JwtUtil;
import com.zentra.common.util.PasswordUtil;
import com.zentra.server.dto.*;
import com.zentra.server.entity.Employee;
import com.zentra.server.mapper.EmployeeMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for EmployeeServiceImpl
 */
@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    private static final Long MERCHANT_ID = 100L;
    private static final Long CURRENT_USER_ID = 1L;

    @Mock
    private EmployeeMapper employeeMapper;

    private EmployeeServiceImpl employeeService;

    private MockedStatic<AuthContext> authContextMock;

    @BeforeEach
    void setUp() {
        employeeService = new EmployeeServiceImpl(employeeMapper);

        authContextMock = mockStatic(AuthContext.class);
        authContextMock.when(AuthContext::getCurrentMerchantId)
                .thenReturn(MERCHANT_ID);
        authContextMock.when(AuthContext::getCurrentUserId)
                .thenReturn(CURRENT_USER_ID);
    }

    @AfterEach
    void tearDown() {
        authContextMock.close();
    }


    // ==================== Create ====================
    @Test
    void create_shouldCreateEmployeeSuccessfully() {
        EmployeeCreateDTO dto = new EmployeeCreateDTO();
        dto.setUsername("john");
        dto.setPassword("Password123");
        dto.setName("John");
        dto.setRole(RoleConstants.SUPER_ADMIN);

        when(employeeMapper.findByUsername("john", MERCHANT_ID))
                .thenReturn(null);

        when(employeeMapper.insert(any(Employee.class)))
                .thenAnswer(invocation -> {
                    Employee employee = invocation.getArgument(0);
                    employee.setId(10L);
                    return 1;
                });

        employeeService.create(dto);

        verify(employeeMapper).findByUsername("john", MERCHANT_ID);

        verify(employeeMapper).insert(argThat(employee ->
                "john".equals(employee.getUsername())
                        && "John".equals(employee.getName())
                        && RoleConstants.SUPER_ADMIN.equals(employee.getRole())
                        && employee.getStatus() == EmployeeStatus.ACTIVE
                        && MERCHANT_ID.equals(employee.getMerchantId())
                        && employee.getPassword() != null
                        && !"Password123".equals(employee.getPassword())
        ));
    }

    @Test
    void create_shouldRejectDuplicateUsername() {
        Employee existingEmployee = new Employee();
        existingEmployee.setId(10L);
        existingEmployee.setUsername("john");
        existingEmployee.setMerchantId(MERCHANT_ID);

        EmployeeCreateDTO dto = new EmployeeCreateDTO();
        dto.setUsername("john");
        dto.setPassword("Password123");
        dto.setName("John");
        dto.setRole(RoleConstants.SUPER_ADMIN);

        when(employeeMapper.findByUsername("john", MERCHANT_ID))
                .thenReturn(existingEmployee);

        assertThatThrownBy(() -> employeeService.create(dto))
                .satisfies(exception ->
                        assertBusinessException(
                                exception,
                                ErrorCode.EMPLOYEE_USERNAME_ALREADY_EXISTS,
                                ErrorMessage.EMPLOYEE_USERNAME_ALREADY_EXISTS
                        )
                );

        verify(employeeMapper).findByUsername("john", MERCHANT_ID);
        verify(employeeMapper, never()).insert(any(Employee.class));
    }

    @Test
    void create_shouldHandleDuplicateKeyException() {
        EmployeeCreateDTO dto = new EmployeeCreateDTO();
        dto.setUsername("john");
        dto.setPassword("Password123");
        dto.setName("John");
        dto.setRole(RoleConstants.SUPER_ADMIN);

        when(employeeMapper.findByUsername("john", MERCHANT_ID))
                .thenReturn(null);

        when(employeeMapper.insert(any(Employee.class)))
                .thenThrow(new DuplicateKeyException("Duplicate employee username"));

        assertThatThrownBy(() -> employeeService.create(dto))
                .satisfies(exception ->
                        assertBusinessException(
                                exception,
                                ErrorCode.EMPLOYEE_USERNAME_ALREADY_EXISTS,
                                ErrorMessage.EMPLOYEE_USERNAME_ALREADY_EXISTS
                        )
                );

        verify(employeeMapper).findByUsername("john", MERCHANT_ID);
        verify(employeeMapper).insert(any(Employee.class));
    }

    @Test
    void create_shouldRejectDuplicateUsernameWhenDatabaseConstraintFails() {
        EmployeeCreateDTO dto = new EmployeeCreateDTO();
        dto.setUsername("john");
        dto.setPassword("Password123");
        dto.setName("John");
        dto.setRole(RoleConstants.SUPER_ADMIN);

        when(employeeMapper.findByUsername("john", MERCHANT_ID))
                .thenReturn(null);

        when(employeeMapper.insert(any(Employee.class)))
                .thenThrow(new DuplicateKeyException("Duplicate username"));

        assertThatThrownBy(() -> employeeService.create(dto))
                .satisfies(exception ->
                        assertBusinessException(
                                exception,
                                ErrorCode.EMPLOYEE_USERNAME_ALREADY_EXISTS,
                                ErrorMessage.EMPLOYEE_USERNAME_ALREADY_EXISTS
                        )
                );

        verify(employeeMapper)
                .findByUsername("john", MERCHANT_ID);

        verify(employeeMapper)
                .insert(any(Employee.class));
    }

    // ==================== Page ====================
    @Test
    void page_shouldReturnEmployeePageSuccessfully() {
        EmployeeQueryDTO queryDTO = new EmployeeQueryDTO();
        queryDTO.setPage(1);
        queryDTO.setPageSize(10);
        queryDTO.setUsername("john");
        queryDTO.setStatus(EmployeeStatus.ACTIVE);

        Employee employee1 = new Employee();
        employee1.setId(10L);
        employee1.setUsername("john");
        employee1.setName("John");
        employee1.setRole(RoleConstants.STORE_MANAGER);
        employee1.setStatus(EmployeeStatus.ACTIVE);
        employee1.setMerchantId(MERCHANT_ID);

        Employee employee2 = new Employee();
        employee2.setId(11L);
        employee2.setUsername("johnny");
        employee2.setName("Johnny");
        employee2.setRole(RoleConstants.STORE_MANAGER);
        employee2.setStatus(EmployeeStatus.ACTIVE);
        employee2.setMerchantId(MERCHANT_ID);

        List<Employee> employees = List.of(employee1, employee2);

        when(employeeMapper.findPage(
                "john",
                EmployeeStatus.ACTIVE,
                MERCHANT_ID,
                0,
                10
        )).thenReturn(employees);

        when(employeeMapper.count(
                "john",
                EmployeeStatus.ACTIVE,
                MERCHANT_ID
        )).thenReturn(2L);

        PageResult<EmployeeDTO> result = employeeService.page(queryDTO);

        assertThat(result)
                .isNotNull();

        assertThat(result.getTotal())
                .isEqualTo(2L);

        assertThat(result.getRecords())
                .hasSize(2)
                .satisfies(records -> {
                    assertThat(records.get(0).getId()).isEqualTo(10L);
                    assertThat(records.get(0).getUsername()).isEqualTo("john");
                    assertThat(records.get(0).getName()).isEqualTo("John");
                    assertThat(records.get(0).getRole())
                            .isEqualTo(RoleConstants.STORE_MANAGER);
                    assertThat(records.get(0).getStatus())
                            .isEqualTo(EmployeeStatus.ACTIVE);

                    assertThat(records.get(1).getId()).isEqualTo(11L);
                    assertThat(records.get(1).getUsername()).isEqualTo("johnny");
                    assertThat(records.get(1).getName()).isEqualTo("Johnny");
                    assertThat(records.get(1).getRole())
                            .isEqualTo(RoleConstants.STORE_MANAGER);
                    assertThat(records.get(1).getStatus())
                            .isEqualTo(EmployeeStatus.ACTIVE);
                });

        verify(employeeMapper).findPage(
                "john",
                EmployeeStatus.ACTIVE,
                MERCHANT_ID,
                0,
                10
        );

        verify(employeeMapper).count(
                "john",
                EmployeeStatus.ACTIVE,
                MERCHANT_ID
        );
    }

    // ==================== Get By ID ====================
    @Test
    void getById_shouldReturnEmployeeSuccessfully() {
        Long employeeId = 10L;

        Employee employee = new Employee();
        employee.setId(employeeId);
        employee.setUsername("john");
        employee.setName("John");
        employee.setRole(RoleConstants.STORE_MANAGER);
        employee.setStatus(EmployeeStatus.ACTIVE);
        employee.setMerchantId(MERCHANT_ID);

        when(employeeMapper.findById(employeeId, MERCHANT_ID))
                .thenReturn(employee);

        EmployeeDTO result = employeeService.getById(employeeId);

        assertThat(result)
                .isNotNull()
                .satisfies(dto -> {
                    assertThat(dto.getId()).isEqualTo(employeeId);
                    assertThat(dto.getUsername()).isEqualTo("john");
                    assertThat(dto.getName()).isEqualTo("John");
                    assertThat(dto.getRole()).isEqualTo(RoleConstants.STORE_MANAGER);
                    assertThat(dto.getStatus()).isEqualTo(EmployeeStatus.ACTIVE);
                });

        verify(employeeMapper).findById(employeeId, MERCHANT_ID);
    }

    @Test
    void getById_shouldRejectEmployeeNotFound() {
        Long employeeId = 999L;

        when(employeeMapper.findById(employeeId, MERCHANT_ID))
                .thenReturn(null);

        assertThatThrownBy(() -> employeeService.getById(employeeId))
                .satisfies(exception ->
                        assertBusinessException(
                                exception,
                                ErrorCode.EMPLOYEE_NOT_FOUND,
                                ErrorMessage.EMPLOYEE_NOT_FOUND
                        )
                );

        verify(employeeMapper).findById(employeeId, MERCHANT_ID);
    }

    // ==================== Get By Username ====================
    @Test
    void getByUsername_shouldReturnEmployeeSuccessfully() {
        String username = "john";

        Employee employee = new Employee();
        employee.setId(10L);
        employee.setUsername(username);
        employee.setName("John");
        employee.setRole(RoleConstants.STORE_MANAGER);
        employee.setStatus(EmployeeStatus.ACTIVE);
        employee.setMerchantId(MERCHANT_ID);

        when(employeeMapper.findByUsername(username, MERCHANT_ID))
                .thenReturn(employee);

        EmployeeDTO result = employeeService.getByUsername(username);

        assertThat(result)
                .isNotNull()
                .satisfies(dto -> {
                    assertThat(dto.getId()).isEqualTo(10L);
                    assertThat(dto.getUsername()).isEqualTo(username);
                    assertThat(dto.getName()).isEqualTo("John");
                    assertThat(dto.getRole()).isEqualTo(RoleConstants.STORE_MANAGER);
                    assertThat(dto.getStatus()).isEqualTo(EmployeeStatus.ACTIVE);
                });

        verify(employeeMapper).findByUsername(username, MERCHANT_ID);
    }

    @Test
    void getByUsername_shouldRejectEmployeeNotFound() {
        String username = "unknown";

        when(employeeMapper.findByUsername(username, MERCHANT_ID))
                .thenReturn(null);

        assertThatThrownBy(() -> employeeService.getByUsername(username))
                .satisfies(exception ->
                        assertBusinessException(
                                exception,
                                ErrorCode.EMPLOYEE_NOT_FOUND,
                                ErrorMessage.EMPLOYEE_NOT_FOUND
                        )
                );

        verify(employeeMapper).findByUsername(username, MERCHANT_ID);
    }

    // ==================== Get Current Employee ====================
    @Test
    void getCurrentEmployee_shouldReturnEmployeeSuccessfully() {
        Employee employee = new Employee();
        employee.setId(CURRENT_USER_ID);
        employee.setUsername("john");
        employee.setName("John");
        employee.setRole(RoleConstants.STORE_MANAGER);
        employee.setStatus(EmployeeStatus.ACTIVE);
        employee.setMerchantId(MERCHANT_ID);

        when(employeeMapper.findById(CURRENT_USER_ID, MERCHANT_ID))
                .thenReturn(employee);

        EmployeeDTO result = employeeService.getCurrentEmployee();

        assertThat(result)
                .isNotNull()
                .satisfies(dto -> {
                    assertThat(dto.getId()).isEqualTo(CURRENT_USER_ID);
                    assertThat(dto.getUsername()).isEqualTo("john");
                    assertThat(dto.getName()).isEqualTo("John");
                    assertThat(dto.getRole()).isEqualTo(RoleConstants.STORE_MANAGER);
                    assertThat(dto.getStatus()).isEqualTo(EmployeeStatus.ACTIVE);
                });

        verify(employeeMapper).findById(CURRENT_USER_ID, MERCHANT_ID);
    }

    @Test
    void getCurrentEmployee_shouldRejectEmployeeNotFound() {
        when(employeeMapper.findById(CURRENT_USER_ID, MERCHANT_ID))
                .thenReturn(null);

        assertThatThrownBy(() -> employeeService.getCurrentEmployee())
                .satisfies(exception ->
                        assertBusinessException(
                                exception,
                                ErrorCode.EMPLOYEE_NOT_FOUND,
                                ErrorMessage.EMPLOYEE_NOT_FOUND
                        )
                );

        verify(employeeMapper).findById(CURRENT_USER_ID, MERCHANT_ID);
    }

    // ==================== Login ====================
    @Test
    void login_shouldLoginSuccessfully() {
        EmployeeLoginDTO dto = new EmployeeLoginDTO();
        dto.setUsername("john");
        dto.setPassword("Password123");

        Employee employee = new Employee();
        employee.setId(10L);
        employee.setUsername("john");
        employee.setPassword("encoded-password");
        employee.setName("John");
        employee.setRole(RoleConstants.SUPER_ADMIN);
        employee.setStatus(EmployeeStatus.ACTIVE);
        employee.setMerchantId(MERCHANT_ID);

        when(employeeMapper.findByUsernameOnly("john"))
                .thenReturn(employee);

        try (MockedStatic<PasswordUtil> passwordUtilMock =
                     mockStatic(PasswordUtil.class);
             MockedStatic<JwtUtil> jwtUtilMock =
                     mockStatic(JwtUtil.class)) {

            passwordUtilMock.when(() ->
                    PasswordUtil.matches(
                            "Password123",
                            "encoded-password"
                    )
            ).thenReturn(true);

            jwtUtilMock.when(() ->
                    JwtUtil.generateToken(any(AuthInfo.class))
            ).thenReturn("jwt-token");

            LoginResponse result = employeeService.login(dto);

            assertThat(result)
                    .isNotNull();

            assertThat(result.getToken())
                    .isEqualTo("jwt-token");

            assertThat(result.getUserId())
                    .isEqualTo(10L);

            verify(employeeMapper)
                    .findByUsernameOnly("john");

            passwordUtilMock.verify(() ->
                    PasswordUtil.matches(
                            "Password123",
                            "encoded-password"
                    )
            );

            jwtUtilMock.verify(() ->
                    JwtUtil.generateToken(any(AuthInfo.class))
            );
        }
    }

    @Test
    void login_shouldRejectUnknownEmployee() {
        EmployeeLoginDTO dto = new EmployeeLoginDTO();
        dto.setUsername("unknown");
        dto.setPassword("Password123");

        when(employeeMapper.findByUsernameOnly("unknown"))
                .thenReturn(null);

        try (MockedStatic<PasswordUtil> passwordUtilMock =
                     mockStatic(PasswordUtil.class);
             MockedStatic<JwtUtil> jwtUtilMock =
                     mockStatic(JwtUtil.class)) {

            assertThatThrownBy(() -> employeeService.login(dto))
                    .satisfies(exception ->
                            assertBusinessException(
                                    exception,
                                    ErrorCode.EMPLOYEE_USERNAME_OR_PASSWORD_ERROR,
                                    ErrorMessage.EMPLOYEE_USERNAME_OR_PASSWORD_ERROR
                            )
                    );

            verify(employeeMapper)
                    .findByUsernameOnly("unknown");

            passwordUtilMock.verifyNoInteractions();

            jwtUtilMock.verifyNoInteractions();
        }
    }

    @Test
    void login_shouldRejectDisabledEmployee() {
        EmployeeLoginDTO dto = new EmployeeLoginDTO();
        dto.setUsername("john");
        dto.setPassword("Password123");

        Employee employee = new Employee();
        employee.setId(10L);
        employee.setUsername("john");
        employee.setPassword("encoded-password");
        employee.setName("John");
        employee.setRole(RoleConstants.SUPER_ADMIN);
        employee.setStatus(EmployeeStatus.DISABLED);
        employee.setMerchantId(MERCHANT_ID);

        when(employeeMapper.findByUsernameOnly("john"))
                .thenReturn(employee);

        try (MockedStatic<PasswordUtil> passwordUtilMock =
                     mockStatic(PasswordUtil.class);
             MockedStatic<JwtUtil> jwtUtilMock =
                     mockStatic(JwtUtil.class)) {

            assertThatThrownBy(() -> employeeService.login(dto))
                    .satisfies(exception ->
                            assertBusinessException(
                                    exception,
                                    ErrorCode.EMPLOYEE_DISABLED,
                                    ErrorMessage.EMPLOYEE_DISABLED
                            )
                    );

            verify(employeeMapper)
                    .findByUsernameOnly("john");

            passwordUtilMock.verifyNoInteractions();

            jwtUtilMock.verifyNoInteractions();
        }
    }

    @Test
    void login_shouldRejectInvalidPassword() {
        EmployeeLoginDTO dto = new EmployeeLoginDTO();
        dto.setUsername("john");
        dto.setPassword("WrongPassword");

        Employee employee = new Employee();
        employee.setId(10L);
        employee.setUsername("john");
        employee.setPassword("encoded-password");
        employee.setName("John");
        employee.setRole(RoleConstants.SUPER_ADMIN);
        employee.setStatus(EmployeeStatus.ACTIVE);
        employee.setMerchantId(MERCHANT_ID);

        when(employeeMapper.findByUsernameOnly("john"))
                .thenReturn(employee);

        try (MockedStatic<PasswordUtil> passwordUtilMock =
                     mockStatic(PasswordUtil.class);
             MockedStatic<JwtUtil> jwtUtilMock =
                     mockStatic(JwtUtil.class)) {

            passwordUtilMock.when(() ->
                    PasswordUtil.matches(
                            "WrongPassword",
                            "encoded-password"
                    )
            ).thenReturn(false);

            assertThatThrownBy(() -> employeeService.login(dto))
                    .satisfies(exception ->
                            assertBusinessException(
                                    exception,
                                    ErrorCode.EMPLOYEE_USERNAME_OR_PASSWORD_ERROR,
                                    ErrorMessage.EMPLOYEE_USERNAME_OR_PASSWORD_ERROR
                            )
                    );

            verify(employeeMapper)
                    .findByUsernameOnly("john");

            passwordUtilMock.verify(() ->
                    PasswordUtil.matches(
                            "WrongPassword",
                            "encoded-password"
                    )
            );

            jwtUtilMock.verifyNoInteractions();
        }
    }

    // ==================== Update ====================
    @Test
    void update_shouldUpdateEmployeeSuccessfully() {
        EmployeeUpdateDTO dto = new EmployeeUpdateDTO();
        dto.setId(10L);
        dto.setUsername("john.updated");
        dto.setName("John Updated");
        dto.setRole(RoleConstants.SUPER_ADMIN);

        Employee existingEmployee = new Employee();
        existingEmployee.setId(10L);
        existingEmployee.setUsername("john");
        existingEmployee.setMerchantId(MERCHANT_ID);

        when(employeeMapper.findById(10L, MERCHANT_ID))
                .thenReturn(existingEmployee);

        when(employeeMapper.update(any(Employee.class)))
                .thenReturn(1);

        employeeService.update(dto);

        verify(employeeMapper).findById(10L, MERCHANT_ID);

        verify(employeeMapper).update(argThat(employee ->
                Long.valueOf(10L).equals(employee.getId())
                        && "john.updated".equals(employee.getUsername())
                        && "John Updated".equals(employee.getName())
                        && RoleConstants.SUPER_ADMIN.equals(employee.getRole())
                        && MERCHANT_ID.equals(employee.getMerchantId())
        ));
    }

    @Test
    void update_shouldRejectEmployeeNotFound() {
        EmployeeUpdateDTO dto = new EmployeeUpdateDTO();
        dto.setId(10L);
        dto.setName("John Updated");

        when(employeeMapper.findById(10L, MERCHANT_ID))
                .thenReturn(null);

        assertThatThrownBy(() -> employeeService.update(dto))
                .satisfies(exception ->
                        assertBusinessException(
                                exception,
                                ErrorCode.EMPLOYEE_NOT_FOUND,
                                ErrorMessage.EMPLOYEE_NOT_FOUND
                        )
                );

        verify(employeeMapper).findById(10L, MERCHANT_ID);
        verify(employeeMapper, never()).update(any(Employee.class));
    }

    @Test
    void update_shouldRejectEmptyUpdate() {
        EmployeeUpdateDTO dto = new EmployeeUpdateDTO();
        dto.setId(10L);

        assertThatThrownBy(() -> employeeService.update(dto))
                .satisfies(exception ->
                        assertBusinessException(
                                exception,
                                ErrorCode.EMPLOYEE_UPDATE_FAILED,
                                ErrorMessage.EMPLOYEE_UPDATE_FAILED
                        )
                );

        verify(employeeMapper, never()).findById(anyLong(), anyLong());
        verify(employeeMapper, never()).update(any(Employee.class));
    }

    @Test
    void update_shouldRejectInvalidRole() {
        EmployeeUpdateDTO dto = new EmployeeUpdateDTO();
        dto.setId(10L);
        dto.setRole("INVALID_ROLE");

        assertThatThrownBy(() -> employeeService.update(dto))
                .satisfies(exception ->
                        assertBusinessException(
                                exception,
                                ErrorCode.INVALID_EMPLOYEE_ROLE,
                                ErrorMessage.INVALID_EMPLOYEE_ROLE
                        )
                );

        verify(employeeMapper, never()).findById(anyLong(), anyLong());
        verify(employeeMapper, never()).update(any(Employee.class));
    }

    @Test
    void update_shouldRejectUpdateFailure() {
        EmployeeUpdateDTO dto = new EmployeeUpdateDTO();
        dto.setId(10L);
        dto.setUsername("john.updated");
        dto.setName("John Updated");
        dto.setRole(RoleConstants.SUPER_ADMIN);

        Employee existingEmployee = new Employee();
        existingEmployee.setId(10L);
        existingEmployee.setUsername("john");
        existingEmployee.setMerchantId(MERCHANT_ID);

        when(employeeMapper.findById(10L, MERCHANT_ID))
                .thenReturn(existingEmployee);

        when(employeeMapper.update(any(Employee.class)))
                .thenReturn(0);

        assertThatThrownBy(() -> employeeService.update(dto))
                .satisfies(exception ->
                        assertBusinessException(
                                exception,
                                ErrorCode.EMPLOYEE_UPDATE_FAILED,
                                ErrorMessage.EMPLOYEE_UPDATE_FAILED
                        )
                );

        verify(employeeMapper)
                .findById(10L, MERCHANT_ID);

        verify(employeeMapper)
                .update(argThat(employee ->
                        Long.valueOf(10L).equals(employee.getId())
                                && "john.updated".equals(employee.getUsername())
                                && "John Updated".equals(employee.getName())
                                && RoleConstants.SUPER_ADMIN.equals(employee.getRole())
                                && MERCHANT_ID.equals(employee.getMerchantId())
                ));
    }

    // ==================== Update Status ====================
    @Test
    void updateStatus_shouldUpdateEmployeeStatusSuccessfully() {
        EmployeeStatusDTO dto = new EmployeeStatusDTO();
        dto.setId(10L);
        dto.setStatus(EmployeeStatus.DISABLED);

        Employee existingEmployee = new Employee();
        existingEmployee.setId(10L);
        existingEmployee.setUsername("john");
        existingEmployee.setMerchantId(MERCHANT_ID);
        existingEmployee.setStatus(EmployeeStatus.ACTIVE);

        when(employeeMapper.findById(10L, MERCHANT_ID))
                .thenReturn(existingEmployee);

        when(employeeMapper.updateStatus(
                10L,
                MERCHANT_ID,
                EmployeeStatus.DISABLED
        )).thenReturn(1);

        employeeService.updateStatus(dto);

        verify(employeeMapper)
                .findById(10L, MERCHANT_ID);

        verify(employeeMapper)
                .updateStatus(
                        10L,
                        MERCHANT_ID,
                        EmployeeStatus.DISABLED
                );
    }

    @Test
    void updateStatus_shouldRejectEmployeeNotFound() {
        EmployeeStatusDTO dto = new EmployeeStatusDTO();
        dto.setId(10L);
        dto.setStatus(EmployeeStatus.DISABLED);

        when(employeeMapper.findById(10L, MERCHANT_ID))
                .thenReturn(null);

        assertThatThrownBy(() -> employeeService.updateStatus(dto))
                .satisfies(exception ->
                        assertBusinessException(
                                exception,
                                ErrorCode.EMPLOYEE_NOT_FOUND,
                                ErrorMessage.EMPLOYEE_NOT_FOUND
                        )
                );

        verify(employeeMapper)
                .findById(10L, MERCHANT_ID);

        verify(employeeMapper, never())
                .updateStatus(
                        anyLong(),
                        anyLong(),
                        anyInt()
                );
    }

    @Test
    void updateStatus_shouldRejectUpdateFailure() {
        EmployeeStatusDTO dto = new EmployeeStatusDTO();
        dto.setId(10L);
        dto.setStatus(EmployeeStatus.DISABLED);

        Employee existingEmployee = new Employee();
        existingEmployee.setId(10L);
        existingEmployee.setUsername("john");
        existingEmployee.setMerchantId(MERCHANT_ID);
        existingEmployee.setStatus(EmployeeStatus.ACTIVE);

        when(employeeMapper.findById(10L, MERCHANT_ID))
                .thenReturn(existingEmployee);

        when(employeeMapper.updateStatus(
                10L,
                MERCHANT_ID,
                EmployeeStatus.DISABLED
        )).thenReturn(0);

        assertThatThrownBy(() -> employeeService.updateStatus(dto))
                .satisfies(exception ->
                        assertBusinessException(
                                exception,
                                ErrorCode.EMPLOYEE_UPDATE_FAILED,
                                ErrorMessage.EMPLOYEE_UPDATE_FAILED
                        )
                );

        verify(employeeMapper)
                .findById(10L, MERCHANT_ID);

        verify(employeeMapper)
                .updateStatus(
                        10L,
                        MERCHANT_ID,
                        EmployeeStatus.DISABLED
                );
    }

    // ==================== Delete By ID ====================
    @Test
    void deleteById_shouldDeleteEmployeeSuccessfully() {
        Long employeeId = 2L;

        Employee employee = new Employee();
        employee.setId(employeeId);
        employee.setUsername("john");
        employee.setMerchantId(MERCHANT_ID);
        employee.setRole(RoleConstants.STORE_MANAGER);

        when(employeeMapper.findById(employeeId, MERCHANT_ID))
                .thenReturn(employee);

        when(employeeMapper.deleteById(employeeId, MERCHANT_ID))
                .thenReturn(1);

        employeeService.deleteById(employeeId);

        verify(employeeMapper).findById(employeeId, MERCHANT_ID);
        verify(employeeMapper).deleteById(employeeId, MERCHANT_ID);
    }

    @Test
    void deleteById_shouldRejectSelfDeletion() {
        Long employeeId = CURRENT_USER_ID;

        Employee employee = new Employee();
        employee.setId(employeeId);
        employee.setUsername("admin");
        employee.setMerchantId(MERCHANT_ID);
        employee.setRole(RoleConstants.SUPER_ADMIN);

        when(employeeMapper.findById(employeeId, MERCHANT_ID))
                .thenReturn(employee);

        assertThatThrownBy(() -> employeeService.deleteById(employeeId))
                .satisfies(exception ->
                        assertBusinessException(
                                exception,
                                ErrorCode.EMPLOYEE_SELF_DELETE_NOT_ALLOWED,
                                ErrorMessage.EMPLOYEE_SELF_DELETE_NOT_ALLOWED
                        )
                );

        verify(employeeMapper).findById(employeeId, MERCHANT_ID);
        verify(employeeMapper, never()).countByRole(anyLong(), anyString());
        verify(employeeMapper, never()).deleteById(anyLong(), anyLong());
    }

    @Test
    void deleteById_shouldRejectDeletingLastSuperAdmin() {
        Long employeeId = 2L;

        Employee employee = new Employee();
        employee.setId(employeeId);
        employee.setUsername("superadmin");
        employee.setMerchantId(MERCHANT_ID);
        employee.setRole(RoleConstants.SUPER_ADMIN);

        when(employeeMapper.findById(employeeId, MERCHANT_ID))
                .thenReturn(employee);

        when(employeeMapper.countByRole(
                MERCHANT_ID,
                RoleConstants.SUPER_ADMIN
        )).thenReturn(1L);

        assertThatThrownBy(() -> employeeService.deleteById(employeeId))
                .satisfies(exception ->
                        assertBusinessException(
                                exception,
                                ErrorCode.LAST_SUPER_ADMIN_DELETE_NOT_ALLOWED,
                                ErrorMessage.LAST_SUPER_ADMIN_DELETE_NOT_ALLOWED
                        )
                );

        verify(employeeMapper).findById(employeeId, MERCHANT_ID);
        verify(employeeMapper).countByRole(
                MERCHANT_ID,
                RoleConstants.SUPER_ADMIN
        );
        verify(employeeMapper, never()).deleteById(anyLong(), anyLong());
    }

    @Test
    void deleteById_shouldRejectEmployeeNotFound() {
        Long employeeId = 999L;

        when(employeeMapper.findById(employeeId, MERCHANT_ID))
                .thenReturn(null);

        assertThatThrownBy(() -> employeeService.deleteById(employeeId))
                .satisfies(exception ->
                        assertBusinessException(
                                exception,
                                ErrorCode.EMPLOYEE_NOT_FOUND,
                                ErrorMessage.EMPLOYEE_NOT_FOUND
                        )
                );

        verify(employeeMapper).findById(employeeId, MERCHANT_ID);
        verify(employeeMapper, never()).countByRole(
                MERCHANT_ID,
                RoleConstants.SUPER_ADMIN
        );
        verify(employeeMapper, never()).deleteById(
                employeeId,
                MERCHANT_ID
        );
    }

    @Test
    void deleteById_shouldRejectDeleteFailure() {
        Long employeeId = 10L;

        Employee existingEmployee = new Employee();
        existingEmployee.setId(employeeId);
        existingEmployee.setUsername("john");
        existingEmployee.setMerchantId(MERCHANT_ID);

        when(employeeMapper.findById(employeeId, MERCHANT_ID))
                .thenReturn(existingEmployee);

        when(employeeMapper.deleteById(employeeId, MERCHANT_ID))
                .thenReturn(0);

        assertThatThrownBy(() -> employeeService.deleteById(employeeId))
                .satisfies(exception ->
                        assertBusinessException(
                                exception,
                                ErrorCode.EMPLOYEE_DELETE_FAILED,
                                ErrorMessage.EMPLOYEE_DELETE_FAILED
                        )
                );

        verify(employeeMapper)
                .findById(employeeId, MERCHANT_ID);

        verify(employeeMapper)
                .deleteById(employeeId, MERCHANT_ID);
    }

    private void assertBusinessException(
            Throwable exception,
            Integer expectedCode,
            String expectedMessage
    ) {
        assertThat(exception)
                .isInstanceOf(BusinessException.class);

        BusinessException businessException =
                (BusinessException) exception;

        assertThat(businessException.getCode())
                .isEqualTo(expectedCode);

        assertThat(businessException.getMessage())
                .isEqualTo(expectedMessage);
    }
}