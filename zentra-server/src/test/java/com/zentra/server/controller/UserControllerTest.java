package com.zentra.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zentra.common.constant.ErrorCode;
import com.zentra.common.constant.ErrorMessage;
import com.zentra.common.exception.BusinessException;
import com.zentra.common.result.PageResult;
import com.zentra.server.dto.*;
import com.zentra.server.exception.GlobalExceptionHandler;
import com.zentra.server.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {

        mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
    }

    // ==================== Register ====================
    @Test
    void register_shouldRegisterUserSuccessfully()
            throws Exception {

        UserRegisterDTO request = new UserRegisterDTO();
        request.setUsername("sultan_bek");
        request.setPassword("123456");
        request.setNickname("Sultan");
        request.setPhone("+77001234567");

        doNothing().when(userService)
                .register(any(UserRegisterDTO.class));

        mockMvc.perform(
                        post("/user/register")
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

        ArgumentCaptor<UserRegisterDTO> captor =
                ArgumentCaptor.forClass(UserRegisterDTO.class);

        verify(userService).register(captor.capture());

        UserRegisterDTO captured = captor.getValue();

        assertThat(captured.getUsername())
                .isEqualTo("sultan_bek");
        assertThat(captured.getPassword())
                .isEqualTo("123456");
        assertThat(captured.getNickname())
                .isEqualTo("Sultan");
        assertThat(captured.getPhone())
                .isEqualTo("+77001234567");
    }

    @Test
    void register_shouldRejectBlankUsername()
            throws Exception {

        UserRegisterDTO request = new UserRegisterDTO();
        request.setUsername("");
        request.setPassword("123456");
        request.setNickname("Sultan");
        request.setPhone("+77001234567");

        mockMvc.perform(
                        post("/user/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code")
                        .value(ErrorCode.FAILURE));

        verifyNoInteractions(userService);
    }

    @Test
    void register_shouldRejectUsernameShorterThanMinimum()
            throws Exception {

        UserRegisterDTO request = new UserRegisterDTO();
        request.setUsername("abc");
        request.setPassword("123456");
        request.setNickname("Sultan");
        request.setPhone("+77001234567");

        mockMvc.perform(
                        post("/user/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code")
                        .value(ErrorCode.FAILURE))
                .andExpect(jsonPath("$.msg")
                        .value("username must be between 4 and 20 characters"));

        verifyNoInteractions(userService);
    }

    @Test
    void register_shouldRejectBlankPassword()
            throws Exception {

        UserRegisterDTO request = new UserRegisterDTO();
        request.setUsername("sultan_bek");
        request.setPassword("");
        request.setNickname("Sultan");
        request.setPhone("+77001234567");

        mockMvc.perform(
                        post("/user/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code")
                        .value(ErrorCode.FAILURE));

        verifyNoInteractions(userService);
    }

    @Test
    void register_shouldRejectInvalidPhoneFormat()
            throws Exception {

        UserRegisterDTO request = new UserRegisterDTO();
        request.setUsername("sultan_bek");
        request.setPassword("123456");
        request.setNickname("Sultan");
        request.setPhone("12345abc");

        mockMvc.perform(
                        post("/user/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code")
                        .value(ErrorCode.FAILURE))
                .andExpect(jsonPath("$.msg")
                        .value("invalid phone number format"));

        verifyNoInteractions(userService);
    }

    @Test
    void register_shouldReturnConflictWhenUsernameAlreadyExists()
            throws Exception {

        UserRegisterDTO request = new UserRegisterDTO();
        request.setUsername("sultan_bek");
        request.setPassword("123456");
        request.setNickname("Sultan");
        request.setPhone("+77001234567");

        doThrow(new BusinessException(
                ErrorCode.USERNAME_ALREADY_EXISTS,
                ErrorMessage.USERNAME_ALREADY_EXISTS
        )).when(userService)
                .register(any(UserRegisterDTO.class));

        mockMvc.perform(
                        post("/user/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code")
                        .value(ErrorCode.USERNAME_ALREADY_EXISTS))
                .andExpect(jsonPath("$.msg")
                        .value(ErrorMessage.USERNAME_ALREADY_EXISTS));

        verify(userService)
                .register(any(UserRegisterDTO.class));
    }

    // ==================== Login ====================
    @Test
    void login_shouldReturnTokenSuccessfully()
            throws Exception {

        UserLoginDTO request = new UserLoginDTO();
        request.setUsername("sultan_bek");
        request.setPassword("123456");
        request.setVerificationCode("123456");

        LoginResponse response =
                new LoginResponse(
                        "test-jwt-token",
                        1L
                );

        when(userService.login(any(UserLoginDTO.class)))
                .thenReturn(response);

        mockMvc.perform(
                        post("/user/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code")
                        .value(1))
                .andExpect(jsonPath("$.msg")
                        .value("success"))
                .andExpect(jsonPath("$.data.token")
                        .value("test-jwt-token"))
                .andExpect(jsonPath("$.data.userId")
                        .value(1));

        ArgumentCaptor<UserLoginDTO> captor =
                ArgumentCaptor.forClass(UserLoginDTO.class);

        verify(userService).login(captor.capture());

        UserLoginDTO captured = captor.getValue();

        assertThat(captured.getUsername())
                .isEqualTo("sultan_bek");
        assertThat(captured.getPassword())
                .isEqualTo("123456");
        assertThat(captured.getVerificationCode())
                .isEqualTo("123456");
    }

    @Test
    void login_shouldRejectBlankVerificationCode()
            throws Exception {

        UserLoginDTO request = new UserLoginDTO();
        request.setUsername("sultan_bek");
        request.setPassword("123456");
        request.setVerificationCode("");

        mockMvc.perform(
                        post("/user/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code")
                        .value(ErrorCode.FAILURE))
                .andExpect(jsonPath("$.msg")
                        .value("Verification code cannot be blank"));

        verifyNoInteractions(userService);
    }

    @Test
    void login_shouldReturnBadRequestWhenVerificationCodeIsInvalid()
            throws Exception {

        UserLoginDTO request = new UserLoginDTO();
        request.setUsername("sultan_bek");
        request.setPassword("123456");
        request.setVerificationCode("999999");

        when(userService.login(any(UserLoginDTO.class)))
                .thenThrow(new BusinessException(
                        ErrorCode.BAD_REQUEST,
                        ErrorMessage.INVALID_VERIFICATION_CODE
                ));

        mockMvc.perform(
                        post("/user/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code")
                        .value(ErrorCode.BAD_REQUEST))
                .andExpect(jsonPath("$.msg")
                        .value(ErrorMessage.INVALID_VERIFICATION_CODE));

        verify(userService)
                .login(any(UserLoginDTO.class));
    }

    // ==================== Logout ====================
    @Test
    void logout_shouldLogoutSuccessfully()
            throws Exception {

        String token = "test-jwt-token";

        doNothing().when(userService)
                .logout(token);

        mockMvc.perform(
                        post("/user/logout")
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code")
                        .value(1))
                .andExpect(jsonPath("$.msg")
                        .value("success"));

        verify(userService)
                .logout(token);
    }

    // ==================== Profile ====================
    @Test
    void profile_shouldReturnCurrentUserSuccessfully()
            throws Exception {

        UserDTO user = new UserDTO();
        user.setId(1L);
        user.setUsername("sultan_bek");
        user.setNickname("Sultan");
        user.setPhone("+77001234567");
        user.setStatus(1);

        when(userService.getProfile())
                .thenReturn(user);

        mockMvc.perform(
                        get("/user/profile")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code")
                        .value(1))
                .andExpect(jsonPath("$.msg")
                        .value("success"))
                .andExpect(jsonPath("$.data.id")
                        .value(1))
                .andExpect(jsonPath("$.data.username")
                        .value("sultan_bek"))
                .andExpect(jsonPath("$.data.nickname")
                        .value("Sultan"))
                .andExpect(jsonPath("$.data.phone")
                        .value("+77001234567"))
                .andExpect(jsonPath("$.data.status")
                        .value(1));

        verify(userService)
                .getProfile();
    }

    @Test
    void profile_shouldReturnNotFoundWhenUserDoesNotExist()
            throws Exception {

        when(userService.getProfile())
                .thenThrow(new BusinessException(
                        ErrorCode.USER_NOT_FOUND,
                        ErrorMessage.USER_NOT_FOUND
                ));

        mockMvc.perform(
                        get("/user/profile")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code")
                        .value(ErrorCode.USER_NOT_FOUND))
                .andExpect(jsonPath("$.msg")
                        .value(ErrorMessage.USER_NOT_FOUND));

        verify(userService)
                .getProfile();
    }

    // ==================== Get User By ID ====================
    @Test
    void getUserById_shouldReturnUserSuccessfully()
            throws Exception {

        UserDTO user = new UserDTO();
        user.setId(1L);
        user.setUsername("sultan_bek");
        user.setNickname("Sultan");
        user.setPhone("+77001234567");
        user.setStatus(1);

        when(userService.getUserById(1L))
                .thenReturn(user);

        mockMvc.perform(
                        get("/user/{id}", 1L)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code")
                        .value(1))
                .andExpect(jsonPath("$.msg")
                        .value("success"))
                .andExpect(jsonPath("$.data.id")
                        .value(1))
                .andExpect(jsonPath("$.data.username")
                        .value("sultan_bek"))
                .andExpect(jsonPath("$.data.nickname")
                        .value("Sultan"))
                .andExpect(jsonPath("$.data.phone")
                        .value("+77001234567"))
                .andExpect(jsonPath("$.data.status")
                        .value(1));

        verify(userService)
                .getUserById(1L);
    }

    @Test
    void getUserById_shouldReturnNotFoundWhenUserDoesNotExist()
            throws Exception {

        when(userService.getUserById(999L))
                .thenThrow(new BusinessException(
                        ErrorCode.USER_NOT_FOUND,
                        ErrorMessage.USER_NOT_FOUND
                ));

        mockMvc.perform(
                        get("/user/{id}", 999L)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code")
                        .value(ErrorCode.USER_NOT_FOUND))
                .andExpect(jsonPath("$.msg")
                        .value(ErrorMessage.USER_NOT_FOUND));

        verify(userService)
                .getUserById(999L);
    }

    // ==================== Update ====================
    @Test
    void updateProfile_shouldUpdateUserSuccessfully()
            throws Exception {

        UserUpdateDTO request = new UserUpdateDTO();
        request.setNickname("Sultan");
        request.setPhone("+77001234567");

        doNothing().when(userService)
                .updateProfile(any(UserUpdateDTO.class));

        mockMvc.perform(
                        put("/user/profile")
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

        ArgumentCaptor<UserUpdateDTO> captor =
                ArgumentCaptor.forClass(UserUpdateDTO.class);

        verify(userService).updateProfile(captor.capture());

        UserUpdateDTO captured = captor.getValue();

        assertThat(captured.getNickname())
                .isEqualTo("Sultan");
        assertThat(captured.getPhone())
                .isEqualTo("+77001234567");
    }

    @Test
    void updateProfile_shouldRejectBlankNickname()
            throws Exception {

        UserUpdateDTO request = new UserUpdateDTO();
        request.setNickname("");
        request.setPhone("+77001234567");

        mockMvc.perform(
                        put("/user/profile")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code")
                        .value(ErrorCode.FAILURE))
                .andExpect(jsonPath("$.msg")
                        .value("Nickname cannot be blank"));

        verifyNoInteractions(userService);
    }

    @Test
    void updateProfile_shouldReturnNotFoundWhenUserDoesNotExist()
            throws Exception {

        UserUpdateDTO request = new UserUpdateDTO();
        request.setNickname("Sultan");
        request.setPhone("+77001234567");

        doThrow(new BusinessException(
                ErrorCode.USER_NOT_FOUND,
                ErrorMessage.USER_NOT_FOUND
        )).when(userService)
                .updateProfile(any(UserUpdateDTO.class));

        mockMvc.perform(
                        put("/user/profile")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code")
                        .value(ErrorCode.USER_NOT_FOUND))
                .andExpect(jsonPath("$.msg")
                        .value(ErrorMessage.USER_NOT_FOUND));

        verify(userService)
                .updateProfile(any(UserUpdateDTO.class));
    }

    // ==================== Get My Orders ====================
    @Test
    void getMyOrders_shouldReturnOrdersSuccessfully()
            throws Exception {

        OrderPageDTO order = new OrderPageDTO();
        order.setId(1L);

        PageResult<OrderPageDTO> pageResult =
                new PageResult<>(
                        1L,
                        List.of(order)
                );

        when(userService.getMyOrders(any(OrderQueryDTO.class)))
                .thenReturn(pageResult);

        mockMvc.perform(
                        get("/user/orders")
                                .param("page", "1")
                                .param("pageSize", "10")
                                .param("status", "1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code")
                        .value(1))
                .andExpect(jsonPath("$.msg")
                        .value("success"))
                .andExpect(jsonPath("$.data.total")
                        .value(1))
                .andExpect(jsonPath("$.data.records[0].id")
                        .value(1));

        ArgumentCaptor<OrderQueryDTO> captor =
                ArgumentCaptor.forClass(OrderQueryDTO.class);

        verify(userService).getMyOrders(captor.capture());

        OrderQueryDTO captured = captor.getValue();

        assertThat(captured.getPage())
                .isEqualTo(1);
        assertThat(captured.getPageSize())
                .isEqualTo(10);
        assertThat(captured.getStatus())
                .isEqualTo(1);
    }

    // ==================== Cancel Order ====================
    @Test
    void cancelOrder_shouldCancelOrderSuccessfully()
            throws Exception {

        Long orderId = 1L;

        doNothing().when(userService)
                .cancelOrder(orderId);

        mockMvc.perform(
                        post("/user/orders/{id}/cancel", orderId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code")
                        .value(1))
                .andExpect(jsonPath("$.msg")
                        .value("success"));

        verify(userService)
                .cancelOrder(orderId);
    }

    @Test
    void cancelOrder_shouldReturnBadRequestWhenOrderCannotBeCancelled()
            throws Exception {

        Long orderId = 1L;

        doThrow(new BusinessException(
                ErrorCode.ORDER_CANNOT_BE_CANCELLED,
                ErrorMessage.ORDER_CANNOT_BE_CANCELLED
        )).when(userService)
                .cancelOrder(orderId);

        mockMvc.perform(
                        post("/user/orders/{id}/cancel", orderId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code")
                        .value(ErrorCode.ORDER_CANNOT_BE_CANCELLED))
                .andExpect(jsonPath("$.msg")
                        .value(ErrorMessage.ORDER_CANNOT_BE_CANCELLED));

        verify(userService)
                .cancelOrder(orderId);
    }

    // ==================== Pay Order ====================
    @Test
    void payOrder_shouldPayOrderSuccessfully()
            throws Exception {

        Long orderId = 1L;

        doNothing().when(userService)
                .payOrder(orderId);

        mockMvc.perform(
                        post("/user/orders/{id}/pay", orderId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code")
                        .value(1))
                .andExpect(jsonPath("$.msg")
                        .value("success"));

        verify(userService)
                .payOrder(orderId);
    }

    @Test
    void payOrder_shouldReturnBadRequestWhenOrderCannotBePaid()
            throws Exception {

        Long orderId = 1L;

        doThrow(new BusinessException(
                ErrorCode.ORDER_CANNOT_BE_PAID,
                ErrorMessage.ORDER_CANNOT_BE_PAID
        )).when(userService)
                .payOrder(orderId);

        mockMvc.perform(
                        post("/user/orders/{id}/pay", orderId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code")
                        .value(ErrorCode.ORDER_CANNOT_BE_PAID))
                .andExpect(jsonPath("$.msg")
                        .value(ErrorMessage.ORDER_CANNOT_BE_PAID));

        verify(userService)
                .payOrder(orderId);
    }
}