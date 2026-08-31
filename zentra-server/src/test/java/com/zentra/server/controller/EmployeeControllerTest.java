package com.zentra.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zentra.common.constant.ErrorCode;
import com.zentra.common.constant.ErrorMessage;
import com.zentra.common.exception.BusinessException;
import com.zentra.common.result.PageResult;
import com.zentra.server.dto.*;
import com.zentra.server.exception.GlobalExceptionHandler;
import com.zentra.server.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class EmployeeControllerTest {

    @Mock
    private EmployeeService employeeService;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        EmployeeController employeeController =
                new EmployeeController(employeeService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(employeeController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
    }

    // ==================== Create ====================
    @Test
    void createEmployee_shouldCreateEmployeeSuccessfully()
            throws Exception {

        EmployeeCreateDTO dto = new EmployeeCreateDTO();

        dto.setUsername("john");
        dto.setPassword("Password123");
        dto.setName("John");
        dto.setRole("SUPER_ADMIN");

        mockMvc.perform(
                post("/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                objectMapper.writeValueAsString(dto)
                        )
        ).andExpect(
                status().isOk()
        );

        ArgumentCaptor<EmployeeCreateDTO> captor =
                ArgumentCaptor.forClass(EmployeeCreateDTO.class);

        verify(employeeService).create(captor.capture());

        EmployeeCreateDTO actual = captor.getValue();

        assertEquals("john", actual.getUsername());
        assertEquals("Password123", actual.getPassword());
        assertEquals("John", actual.getName());
        assertEquals("SUPER_ADMIN", actual.getRole());
    }

    @Test
    void createEmployee_shouldRejectInvalidRequest()
            throws Exception {

        EmployeeCreateDTO request = new EmployeeCreateDTO();
        request.setUsername("abc");
        request.setPassword("123456");
        request.setName("Test Employee");
        request.setRole("CASHIER");

        mockMvc.perform(
                        post("/employee")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ErrorCode.FAILURE))
                .andExpect(jsonPath("$.msg")
                        .value("username must be between 4 and 20 characters"));

        verifyNoInteractions(employeeService);
    }

    @Test
    void createEmployee_shouldReturnConflictWhenUsernameAlreadyExists()
            throws Exception {

        EmployeeCreateDTO request = new EmployeeCreateDTO();
        request.setUsername("john");
        request.setPassword("123456");
        request.setName("John");
        request.setRole("CASHIER");

        doThrow(new BusinessException(
                ErrorCode.EMPLOYEE_USERNAME_ALREADY_EXISTS,
                ErrorMessage.EMPLOYEE_USERNAME_ALREADY_EXISTS
        )).when(employeeService).create(any(EmployeeCreateDTO.class));

        mockMvc.perform(
                        post("/employee")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code")
                        .value(ErrorCode.EMPLOYEE_USERNAME_ALREADY_EXISTS))
                .andExpect(jsonPath("$.msg")
                        .value(ErrorMessage.EMPLOYEE_USERNAME_ALREADY_EXISTS));

        verify(employeeService).create(any(EmployeeCreateDTO.class));
    }

    // ==================== Page ====================
    @Test
    void page_shouldReturnEmployeeListSuccessfully()
            throws Exception {

        EmployeeDTO employee1 = new EmployeeDTO();
        employee1.setId(1L);
        employee1.setUsername("john");
        employee1.setName("John");
        employee1.setRole("CASHIER");
        employee1.setStatus(1);

        EmployeeDTO employee2 = new EmployeeDTO();
        employee2.setId(2L);
        employee2.setUsername("alice");
        employee2.setName("Alice");
        employee2.setRole("STORE_MANAGER");
        employee2.setStatus(1);

        PageResult<EmployeeDTO> pageResult =
                new PageResult<>(
                        2L,
                        List.of(employee1, employee2)
                );

        when(employeeService.page(any(EmployeeQueryDTO.class)))
                .thenReturn(pageResult);

        mockMvc.perform(
                        get("/employee")
                                .param("page", "1")
                                .param("pageSize", "10")
                                .param("username", "john")
                                .param("status", "1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS))
                .andExpect(jsonPath("$.data.total").value(2))
                .andExpect(jsonPath("$.data.records.length()").value(2))
                .andExpect(jsonPath("$.data.records[0].id").value(1))
                .andExpect(jsonPath("$.data.records[0].username").value("john"))
                .andExpect(jsonPath("$.data.records[0].name").value("John"))
                .andExpect(jsonPath("$.data.records[0].role").value("CASHIER"))
                .andExpect(jsonPath("$.data.records[0].status").value(1))
                .andExpect(jsonPath("$.data.records[1].id").value(2))
                .andExpect(jsonPath("$.data.records[1].username").value("alice"))
                .andExpect(jsonPath("$.data.records[1].name").value("Alice"))
                .andExpect(jsonPath("$.data.records[1].role").value("STORE_MANAGER"))
                .andExpect(jsonPath("$.data.records[1].status").value(1));

        ArgumentCaptor<EmployeeQueryDTO> captor =
                ArgumentCaptor.forClass(EmployeeQueryDTO.class);

        verify(employeeService).page(captor.capture());

        EmployeeQueryDTO actual = captor.getValue();

        assertEquals(1, actual.getPage());
        assertEquals(10, actual.getPageSize());
        assertEquals("john", actual.getUsername());
        assertEquals(1, actual.getStatus());
    }

    // ==================== Get By ID ====================
    @Test
    void getById_shouldReturnEmployeeSuccessfully()
            throws Exception {

        EmployeeDTO employee = new EmployeeDTO();

        employee.setId(1L);
        employee.setUsername("john");
        employee.setName("John");
        employee.setRole("CASHIER");
        employee.setStatus(1);

        when(employeeService.getById(1L))
                .thenReturn(employee);

        mockMvc.perform(
                        get("/employee/1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code")
                        .value(ErrorCode.SUCCESS))
                .andExpect(jsonPath("$.data.id")
                        .value(1))
                .andExpect(jsonPath("$.data.username")
                        .value("john"))
                .andExpect(jsonPath("$.data.name")
                        .value("John"))
                .andExpect(jsonPath("$.data.role")
                        .value("CASHIER"))
                .andExpect(jsonPath("$.data.status")
                        .value(1));

        verify(employeeService)
                .getById(1L);
    }

    @Test
    void getById_shouldReturnNotFoundWhenEmployeeDoesNotExist()
            throws Exception {

        doThrow(new BusinessException(
                ErrorCode.EMPLOYEE_NOT_FOUND,
                ErrorMessage.EMPLOYEE_NOT_FOUND
        )).when(employeeService)
                .getById(999L);

        mockMvc.perform(
                        get("/employee/999")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code")
                        .value(ErrorCode.EMPLOYEE_NOT_FOUND))
                .andExpect(jsonPath("$.msg")
                        .value(ErrorMessage.EMPLOYEE_NOT_FOUND));

        verify(employeeService)
                .getById(999L);
    }

    // ==================== Get By Username ====================
    @Test
    void getByUsername_shouldReturnEmployeeSuccessfully()
            throws Exception {

        EmployeeDTO employee = new EmployeeDTO();

        employee.setId(1L);
        employee.setUsername("john");
        employee.setName("John");
        employee.setRole("CASHIER");
        employee.setStatus(1);

        when(employeeService.getByUsername("john"))
                .thenReturn(employee);

        mockMvc.perform(
                        get("/employee/search")
                                .param("username", "john")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code")
                        .value(ErrorCode.SUCCESS))
                .andExpect(jsonPath("$.data.id")
                        .value(1))
                .andExpect(jsonPath("$.data.username")
                        .value("john"))
                .andExpect(jsonPath("$.data.name")
                        .value("John"))
                .andExpect(jsonPath("$.data.role")
                        .value("CASHIER"))
                .andExpect(jsonPath("$.data.status")
                        .value(1));

        verify(employeeService)
                .getByUsername("john");
    }

    // ==================== Get Current Employee ====================
    @Test
    void getCurrentEmployee_shouldReturnCurrentEmployeeSuccessfully()
            throws Exception {

        EmployeeDTO employee = new EmployeeDTO();

        employee.setId(1L);
        employee.setUsername("john");
        employee.setName("John");
        employee.setRole("CASHIER");
        employee.setStatus(1);

        when(employeeService.getCurrentEmployee())
                .thenReturn(employee);

        mockMvc.perform(
                        get("/employee/me")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code")
                        .value(ErrorCode.SUCCESS))
                .andExpect(jsonPath("$.data.id")
                        .value(1))
                .andExpect(jsonPath("$.data.username")
                        .value("john"))
                .andExpect(jsonPath("$.data.name")
                        .value("John"))
                .andExpect(jsonPath("$.data.role")
                        .value("CASHIER"))
                .andExpect(jsonPath("$.data.status")
                        .value(1));

        verify(employeeService)
                .getCurrentEmployee();
    }

    // ==================== Login ====================
    @Test
    void login_shouldReturnLoginResponseSuccessfully()
            throws Exception {

        EmployeeLoginDTO request = new EmployeeLoginDTO();
        request.setUsername("john");
        request.setPassword("Password123");

        LoginResponse response =
                new LoginResponse(
                        "test-jwt-token",
                        1L
                );

        when(employeeService.login(any(EmployeeLoginDTO.class)))
                .thenReturn(response);

        mockMvc.perform(
                        post("/employee/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code")
                        .value(ErrorCode.SUCCESS))
                .andExpect(jsonPath("$.data.token")
                        .value("test-jwt-token"))
                .andExpect(jsonPath("$.data.userId")
                        .value(1));

        ArgumentCaptor<EmployeeLoginDTO> captor =
                ArgumentCaptor.forClass(EmployeeLoginDTO.class);

        verify(employeeService)
                .login(captor.capture());

        EmployeeLoginDTO actual = captor.getValue();

        assertEquals("john", actual.getUsername());
        assertEquals("Password123", actual.getPassword());
    }

    @Test
    void login_shouldRejectBlankUsername()
            throws Exception {

        EmployeeLoginDTO request = new EmployeeLoginDTO();
        request.setUsername("");
        request.setPassword("Password123");

        mockMvc.perform(
                        post("/employee/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code")
                        .value(ErrorCode.FAILURE))
                .andExpect(jsonPath("$.msg")
                        .value("username cannot be blank"));

        verifyNoInteractions(employeeService);
    }

    @Test
    void login_shouldRejectBlankPassword()
            throws Exception {

        EmployeeLoginDTO request = new EmployeeLoginDTO();
        request.setUsername("john");
        request.setPassword("");

        mockMvc.perform(
                        post("/employee/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code")
                        .value(ErrorCode.FAILURE))
                .andExpect(jsonPath("$.msg")
                        .value("password cannot be blank"));

        verifyNoInteractions(employeeService);
    }

    @Test
    void login_shouldReturnErrorWhenEmployeeDoesNotExist()
            throws Exception {

        EmployeeLoginDTO request = new EmployeeLoginDTO();
        request.setUsername("unknown");
        request.setPassword("Password123");

        doThrow(new BusinessException(
                ErrorCode.EMPLOYEE_USERNAME_OR_PASSWORD_ERROR,
                ErrorMessage.EMPLOYEE_USERNAME_OR_PASSWORD_ERROR
        )).when(employeeService)
                .login(any(EmployeeLoginDTO.class));

        mockMvc.perform(
                        post("/employee/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code")
                        .value(ErrorCode.EMPLOYEE_USERNAME_OR_PASSWORD_ERROR))
                .andExpect(jsonPath("$.msg")
                        .value(ErrorMessage.EMPLOYEE_USERNAME_OR_PASSWORD_ERROR));

        verify(employeeService)
                .login(any(EmployeeLoginDTO.class));
    }

    @Test
    void login_shouldReturnErrorWhenEmployeeIsDisabled()
            throws Exception {

        EmployeeLoginDTO request = new EmployeeLoginDTO();
        request.setUsername("john");
        request.setPassword("Password123");

        doThrow(new BusinessException(
                ErrorCode.EMPLOYEE_DISABLED,
                ErrorMessage.EMPLOYEE_DISABLED
        )).when(employeeService)
                .login(any(EmployeeLoginDTO.class));

        mockMvc.perform(
                        post("/employee/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code")
                        .value(ErrorCode.EMPLOYEE_DISABLED))
                .andExpect(jsonPath("$.msg")
                        .value(ErrorMessage.EMPLOYEE_DISABLED));

        verify(employeeService)
                .login(any(EmployeeLoginDTO.class));
    }

    // ==================== Update Employee ====================
    @Test
    void updateEmployee_shouldUpdateEmployeeSuccessfully() throws Exception {
        EmployeeUpdateDTO request = new EmployeeUpdateDTO();
        request.setId(1L);
        request.setUsername("updatedUser");
        request.setName("Updated User");
        request.setRole("STORE_MANAGER");

        mockMvc.perform(
                        patch("/employee")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk());

        ArgumentCaptor<EmployeeUpdateDTO> captor =
                ArgumentCaptor.forClass(EmployeeUpdateDTO.class);

        verify(employeeService).update(captor.capture());

        EmployeeUpdateDTO captured = captor.getValue();

        assertThat(captured.getId()).isEqualTo(1L);
        assertThat(captured.getUsername()).isEqualTo("updatedUser");
        assertThat(captured.getName()).isEqualTo("Updated User");
        assertThat(captured.getRole()).isEqualTo("STORE_MANAGER");
    }

    @Test
    void updateEmployee_shouldRejectRequestWithoutId() throws Exception {
        EmployeeUpdateDTO request = new EmployeeUpdateDTO();
        request.setUsername("updatedUser");
        request.setName("Updated User");
        request.setRole("STORE_MANAGER");

        mockMvc.perform(
                        patch("/employee")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code")
                        .value(ErrorCode.FAILURE))
                .andExpect(jsonPath("$.msg")
                        .value("employee id cannot be null"));

        verifyNoInteractions(employeeService);
    }

    @Test
    void updateEmployee_shouldRejectInvalidUsername() throws Exception {
        EmployeeUpdateDTO request = new EmployeeUpdateDTO();
        request.setId(1L);
        request.setUsername("abc");
        request.setName("Updated User");
        request.setRole("STORE_MANAGER");

        mockMvc.perform(
                        patch("/employee")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code")
                        .value(ErrorCode.FAILURE))
                .andExpect(jsonPath("$.msg")
                        .value("username must be between 4 and 20 characters"));

        verifyNoInteractions(employeeService);
    }

    @Test
    void updateEmployee_shouldAcceptUsernameWithExactly20Characters()
            throws Exception {

        EmployeeUpdateDTO request = new EmployeeUpdateDTO();
        request.setId(1L);
        request.setUsername("abcdefghijklmnopqrst");
        request.setName("Updated User");
        request.setRole("STORE_MANAGER");

        mockMvc.perform(
                        patch("/employee")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code")
                        .value(ErrorCode.SUCCESS));

        ArgumentCaptor<EmployeeUpdateDTO> captor =
                ArgumentCaptor.forClass(EmployeeUpdateDTO.class);

        verify(employeeService).update(captor.capture());

        EmployeeUpdateDTO actual = captor.getValue();

        assertThat(actual.getId()).isEqualTo(1L);
        assertThat(actual.getUsername())
                .isEqualTo("abcdefghijklmnopqrst");
        assertThat(actual.getName())
                .isEqualTo("Updated User");
        assertThat(actual.getRole())
                .isEqualTo("STORE_MANAGER");
    }

    @Test
    void updateEmployee_shouldRejectNameLongerThan50Characters()
            throws Exception {

        EmployeeUpdateDTO request = new EmployeeUpdateDTO();
        request.setId(1L);
        request.setUsername("updatedUser");
        request.setName("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXY");
        request.setRole("STORE_MANAGER");

        mockMvc.perform(
                        patch("/employee")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code")
                        .value(ErrorCode.FAILURE))
                .andExpect(jsonPath("$.msg")
                        .value("name must be less than 50 characters"));

        verifyNoInteractions(employeeService);
    }

    @Test
    void updateEmployee_shouldReturnNotFoundWhenEmployeeDoesNotExist()
            throws Exception {

        EmployeeUpdateDTO request = new EmployeeUpdateDTO();
        request.setId(999L);
        request.setUsername("updatedUser");
        request.setName("Updated User");
        request.setRole("STORE_MANAGER");

        doThrow(new BusinessException(
                ErrorCode.EMPLOYEE_NOT_FOUND,
                ErrorMessage.EMPLOYEE_NOT_FOUND
        )).when(employeeService)
                .update(any(EmployeeUpdateDTO.class));

        mockMvc.perform(
                        patch("/employee")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code")
                        .value(ErrorCode.EMPLOYEE_NOT_FOUND))
                .andExpect(jsonPath("$.msg")
                        .value(ErrorMessage.EMPLOYEE_NOT_FOUND));

        verify(employeeService).update(any(EmployeeUpdateDTO.class));
    }

    // ==================== Update Employee Status ====================
    @Test
    void updateEmployeeStatus_shouldUpdateSuccessfully() throws Exception {

        EmployeeStatusDTO request = new EmployeeStatusDTO();
        request.setId(1L);
        request.setStatus(1);

        doNothing().when(employeeService)
                .updateStatus(any(EmployeeStatusDTO.class));

        mockMvc.perform(
                        put("/employee/status")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code")
                        .value(1))
                .andExpect(jsonPath("$.msg")
                        .value("success"));

        verify(employeeService)
                .updateStatus(any(EmployeeStatusDTO.class));
    }

    @Test
    void updateEmployeeStatus_shouldRejectRequestWithoutId()
            throws Exception {

        EmployeeStatusDTO request = new EmployeeStatusDTO();
        request.setStatus(1);

        mockMvc.perform(
                        put("/employee/status")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code")
                        .value(ErrorCode.FAILURE))
                .andExpect(jsonPath("$.msg")
                        .value("employee id cannot be null"));

        verifyNoInteractions(employeeService);
    }

    @Test
    void updateEmployeeStatus_shouldRejectRequestWithoutStatus()
            throws Exception {

        EmployeeStatusDTO request = new EmployeeStatusDTO();
        request.setId(1L);

        mockMvc.perform(
                        put("/employee/status")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code")
                        .value(ErrorCode.FAILURE))
                .andExpect(jsonPath("$.msg")
                        .value("status cannot be null"));

        verifyNoInteractions(employeeService);
    }

    @Test
    void updateEmployeeStatus_shouldReturnNotFoundWhenEmployeeDoesNotExist()
            throws Exception {

        EmployeeStatusDTO request = new EmployeeStatusDTO();
        request.setId(999L);
        request.setStatus(1);

        doThrow(new BusinessException(
                ErrorCode.EMPLOYEE_NOT_FOUND,
                ErrorMessage.EMPLOYEE_NOT_FOUND
        )).when(employeeService)
                .updateStatus(any(EmployeeStatusDTO.class));

        mockMvc.perform(
                        put("/employee/status")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code")
                        .value(ErrorCode.EMPLOYEE_NOT_FOUND))
                .andExpect(jsonPath("$.msg")
                        .value(ErrorMessage.EMPLOYEE_NOT_FOUND));

        verify(employeeService)
                .updateStatus(any(EmployeeStatusDTO.class));
    }

    // ==================== Delete Employee ====================
    @Test
    void deleteEmployee_shouldDeleteSuccessfully()
            throws Exception {

        Long employeeId = 2L;

        doNothing().when(employeeService)
                .deleteById(employeeId);

        mockMvc.perform(
                        delete("/employee/{id}", employeeId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code")
                        .value(1))
                .andExpect(jsonPath("$.msg")
                        .value("success"));

        verify(employeeService)
                .deleteById(employeeId);
    }

    @Test
    void deleteEmployee_shouldReturnNotFoundWhenEmployeeDoesNotExist()
            throws Exception {

        Long employeeId = 999L;

        doThrow(new BusinessException(
                ErrorCode.EMPLOYEE_NOT_FOUND,
                ErrorMessage.EMPLOYEE_NOT_FOUND
        )).when(employeeService)
                .deleteById(employeeId);

        mockMvc.perform(
                        delete("/employee/{id}", employeeId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code")
                        .value(ErrorCode.EMPLOYEE_NOT_FOUND))
                .andExpect(jsonPath("$.msg")
                        .value(ErrorMessage.EMPLOYEE_NOT_FOUND));

        verify(employeeService)
                .deleteById(employeeId);
    }

}