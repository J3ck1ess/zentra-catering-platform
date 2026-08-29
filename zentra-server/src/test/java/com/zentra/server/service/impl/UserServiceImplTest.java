package com.zentra.server.service.impl;

import com.zentra.common.constant.*;
import com.zentra.common.context.AuthContext;
import com.zentra.common.exception.BusinessException;
import com.zentra.common.result.PageResult;
import com.zentra.common.util.JwtUtil;
import com.zentra.common.util.PasswordUtil;
import com.zentra.server.dto.*;
import com.zentra.server.entity.Order;
import com.zentra.server.entity.User;
import com.zentra.server.mapper.OrderMapper;
import com.zentra.server.mapper.UserMapper;
import com.zentra.server.service.JwtBlacklistService;
import com.zentra.server.service.RedisService;
import com.zentra.server.service.VerificationCodeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    private static final Long MERCHANT_ID = 1L;

    @Mock
    private UserMapper userMapper;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private VerificationCodeService verificationCodeService;

    @Mock
    private RedisService redisService;

    @Mock
    private JwtBlacklistService jwtBlacklistService;

    @InjectMocks
    private UserServiceImpl userService;

    // ==================== Register ====================
    @Test
    void register_shouldRegisterUserSuccessfully() {
        UserRegisterDTO dto = new UserRegisterDTO();
        dto.setUsername("sultan_bek");
        dto.setPassword("123456");
        dto.setNickname("Sultan");
        dto.setPhone("+77001234567");

        when(userMapper.findByUsername(
                dto.getUsername(),
                MERCHANT_ID
        )).thenReturn(null);

        when(userMapper.insert(any(User.class)))
                .thenReturn(1);

        userService.register(dto);

        verify(userMapper)
                .findByUsername(
                        dto.getUsername(),
                        MERCHANT_ID
                );

        verify(userMapper)
                .insert(argThat(user ->
                        MERCHANT_ID.equals(user.getMerchantId())
                                && dto.getUsername().equals(user.getUsername())
                                && dto.getNickname().equals(user.getNickname())
                                && dto.getPhone().equals(user.getPhone())
                                && user.getStatus() == UserStatus.ACTIVE
                                && user.getPassword() != null
                                && !dto.getPassword().equals(user.getPassword())
                ));
    }

    @Test
    void register_shouldRejectWhenUsernameAlreadyExists() {
        UserRegisterDTO dto = new UserRegisterDTO();
        dto.setUsername("sultan_bek");
        dto.setPassword("123456");
        dto.setNickname("Sultan");
        dto.setPhone("+77001234567");

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername(dto.getUsername());
        existingUser.setMerchantId(MERCHANT_ID);

        when(userMapper.findByUsername(
                dto.getUsername(),
                MERCHANT_ID
        )).thenReturn(existingUser);

        assertThatThrownBy(() -> userService.register(dto))
                .satisfies(exception ->
                        assertBusinessException(
                                exception,
                                ErrorCode.USERNAME_ALREADY_EXISTS,
                                ErrorMessage.USERNAME_ALREADY_EXISTS
                        )
                );

        verify(userMapper)
                .findByUsername(
                        dto.getUsername(),
                        MERCHANT_ID
                );

        verify(userMapper, never())
                .insert(any(User.class));
    }

    @Test
    void register_shouldRejectWhenInsertFails() {
        UserRegisterDTO dto = new UserRegisterDTO();
        dto.setUsername("sultan_bek");
        dto.setPassword("123456");
        dto.setNickname("Sultan");
        dto.setPhone("+77001234567");

        when(userMapper.findByUsername(
                dto.getUsername(),
                MERCHANT_ID
        )).thenReturn(null);

        when(userMapper.insert(any(User.class)))
                .thenReturn(0);

        assertThatThrownBy(() -> userService.register(dto))
                .satisfies(exception ->
                        assertBusinessException(
                                exception,
                                ErrorCode.USER_REGISTER_FAILED,
                                ErrorMessage.USER_REGISTER_FAILED
                        )
                );

        verify(userMapper)
                .findByUsername(
                        dto.getUsername(),
                        MERCHANT_ID
                );

        verify(userMapper)
                .insert(any(User.class));
    }

    // ==================== Login ====================
    @Test
    void login_shouldLoginSuccessfully() {
        UserLoginDTO dto = new UserLoginDTO();
        dto.setUsername("sultan_bek");
        dto.setPassword("123456");
        dto.setVerificationCode("123456");

        User user = new User();
        user.setId(1L);
        user.setMerchantId(MERCHANT_ID);
        user.setUsername("sultan_bek");
        user.setPassword(PasswordUtil.encode("123456"));
        user.setStatus(UserStatus.ACTIVE);

        when(redisService.increment(
                anyString(),
                any(Duration.class)
        )).thenReturn(1L);

        when(verificationCodeService.isRetryAllowed(
                VerificationCodeType.LOGIN,
                dto.getUsername()
        )).thenReturn(true);

        when(verificationCodeService.validateCode(
                VerificationCodeType.LOGIN,
                dto.getUsername(),
                dto.getVerificationCode()
        )).thenReturn(true);

        when(userMapper.findByUsernameOnly(
                dto.getUsername()
        )).thenReturn(user);

        LoginResponse result = userService.login(dto);

        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(user.getId());
        assertThat(result.getToken()).isNotBlank();

        verify(redisService)
                .increment(
                        anyString(),
                        any(Duration.class)
                );

        verify(verificationCodeService)
                .isRetryAllowed(
                        VerificationCodeType.LOGIN,
                        dto.getUsername()
                );

        verify(verificationCodeService)
                .validateCode(
                        VerificationCodeType.LOGIN,
                        dto.getUsername(),
                        dto.getVerificationCode()
                );

        verify(userMapper)
                .findByUsernameOnly(
                        dto.getUsername()
                );

        verify(verificationCodeService)
                .deleteCode(
                        VerificationCodeType.LOGIN,
                        dto.getUsername()
                );
    }

    @Test
    void login_shouldRejectWhenLoginRateLimitExceeded() {
        UserLoginDTO dto = new UserLoginDTO();
        dto.setUsername("sultan_bek");
        dto.setPassword("123456");
        dto.setVerificationCode("123456");

        when(redisService.increment(
                anyString(),
                any(Duration.class)
        )).thenReturn(11L);

        assertThatThrownBy(() -> userService.login(dto))
                .satisfies(exception ->
                        assertBusinessException(
                                exception,
                                ErrorCode.TOO_MANY_REQUESTS,
                                ErrorMessage.TOO_MANY_LOGIN_REQUESTS
                        )
                );

        verify(redisService)
                .increment(
                        anyString(),
                        any(Duration.class)
                );

        verify(verificationCodeService, never())
                .isRetryAllowed(
                        VerificationCodeType.LOGIN,
                        dto.getUsername()
                );

        verify(verificationCodeService, never())
                .validateCode(
                        VerificationCodeType.LOGIN,
                        dto.getUsername(),
                        dto.getVerificationCode()
                );

        verify(userMapper, never())
                .findByUsernameOnly(
                        dto.getUsername()
                );
    }

    @Test
    void login_shouldRejectWhenVerificationRetryLimitExceeded() {
        UserLoginDTO dto = new UserLoginDTO();
        dto.setUsername("sultan_bek");
        dto.setPassword("123456");
        dto.setVerificationCode("123456");

        when(redisService.increment(
                anyString(),
                any(Duration.class)
        )).thenReturn(1L);

        when(verificationCodeService.isRetryAllowed(
                VerificationCodeType.LOGIN,
                dto.getUsername()
        )).thenReturn(false);

        assertThatThrownBy(() -> userService.login(dto))
                .satisfies(exception ->
                        assertBusinessException(
                                exception,
                                ErrorCode.BAD_REQUEST,
                                ErrorMessage.TOO_MANY_VERIFICATION_ATTEMPTS
                        )
                );

        verify(redisService)
                .increment(
                        anyString(),
                        any(Duration.class)
                );

        verify(verificationCodeService)
                .isRetryAllowed(
                        VerificationCodeType.LOGIN,
                        dto.getUsername()
                );

        verify(verificationCodeService, never())
                .validateCode(
                        VerificationCodeType.LOGIN,
                        dto.getUsername(),
                        dto.getVerificationCode()
                );

        verify(userMapper, never())
                .findByUsernameOnly(
                        dto.getUsername()
                );
    }

    @Test
    void login_shouldRejectWhenVerificationCodeIsInvalid() {
        UserLoginDTO dto = new UserLoginDTO();
        dto.setUsername("sultan_bek");
        dto.setPassword("123456");
        dto.setVerificationCode("999999");

        when(redisService.increment(
                anyString(),
                any(Duration.class)
        )).thenReturn(1L);

        when(verificationCodeService.isRetryAllowed(
                VerificationCodeType.LOGIN,
                dto.getUsername()
        )).thenReturn(true);

        when(verificationCodeService.validateCode(
                VerificationCodeType.LOGIN,
                dto.getUsername(),
                dto.getVerificationCode()
        )).thenReturn(false);

        assertThatThrownBy(() -> userService.login(dto))
                .satisfies(exception ->
                        assertBusinessException(
                                exception,
                                ErrorCode.BAD_REQUEST,
                                ErrorMessage.INVALID_VERIFICATION_CODE
                        )
                );

        verify(redisService)
                .increment(
                        anyString(),
                        any(Duration.class)
                );

        verify(verificationCodeService)
                .isRetryAllowed(
                        VerificationCodeType.LOGIN,
                        dto.getUsername()
                );

        verify(verificationCodeService)
                .validateCode(
                        VerificationCodeType.LOGIN,
                        dto.getUsername(),
                        dto.getVerificationCode()
                );

        verify(verificationCodeService)
                .incrementRetryCount(
                        VerificationCodeType.LOGIN,
                        dto.getUsername()
                );

        verify(userMapper, never())
                .findByUsernameOnly(
                        dto.getUsername()
                );

        verify(verificationCodeService, never())
                .deleteCode(
                        VerificationCodeType.LOGIN,
                        dto.getUsername()
                );
    }

    @Test
    void login_shouldRejectWhenUserNotFound() {
        UserLoginDTO dto = new UserLoginDTO();
        dto.setUsername("unknown_user");
        dto.setPassword("123456");
        dto.setVerificationCode("123456");

        when(redisService.increment(
                anyString(),
                any(Duration.class)
        )).thenReturn(1L);

        when(verificationCodeService.isRetryAllowed(
                VerificationCodeType.LOGIN,
                dto.getUsername()
        )).thenReturn(true);

        when(verificationCodeService.validateCode(
                VerificationCodeType.LOGIN,
                dto.getUsername(),
                dto.getVerificationCode()
        )).thenReturn(true);

        when(userMapper.findByUsernameOnly(
                dto.getUsername()
        )).thenReturn(null);

        assertThatThrownBy(() -> userService.login(dto))
                .satisfies(exception ->
                        assertBusinessException(
                                exception,
                                ErrorCode.USERNAME_OR_PASSWORD_ERROR,
                                ErrorMessage.USERNAME_OR_PASSWORD_ERROR
                        )
                );

        verify(redisService)
                .increment(
                        anyString(),
                        any(Duration.class)
                );

        verify(verificationCodeService)
                .isRetryAllowed(
                        VerificationCodeType.LOGIN,
                        dto.getUsername()
                );

        verify(verificationCodeService)
                .validateCode(
                        VerificationCodeType.LOGIN,
                        dto.getUsername(),
                        dto.getVerificationCode()
                );

        verify(userMapper)
                .findByUsernameOnly(
                        dto.getUsername()
                );

        verify(verificationCodeService, never())
                .deleteCode(
                        VerificationCodeType.LOGIN,
                        dto.getUsername()
                );
    }

    @Test
    void login_shouldRejectWhenUserIsDisabled() {
        UserLoginDTO dto = new UserLoginDTO();
        dto.setUsername("disabled_user");
        dto.setPassword("123456");
        dto.setVerificationCode("123456");

        User disabledUser = new User();
        disabledUser.setId(1L);
        disabledUser.setMerchantId(MERCHANT_ID);
        disabledUser.setUsername(dto.getUsername());
        disabledUser.setPassword(PasswordUtil.encode(dto.getPassword()));
        disabledUser.setStatus(UserStatus.DISABLED);

        when(redisService.increment(
                anyString(),
                any(Duration.class)
        )).thenReturn(1L);

        when(verificationCodeService.isRetryAllowed(
                VerificationCodeType.LOGIN,
                dto.getUsername()
        )).thenReturn(true);

        when(verificationCodeService.validateCode(
                VerificationCodeType.LOGIN,
                dto.getUsername(),
                dto.getVerificationCode()
        )).thenReturn(true);

        when(userMapper.findByUsernameOnly(
                dto.getUsername()
        )).thenReturn(disabledUser);

        assertThatThrownBy(() -> userService.login(dto))
                .satisfies(exception ->
                        assertBusinessException(
                                exception,
                                ErrorCode.USER_DISABLED,
                                ErrorMessage.USER_DISABLED
                        )
                );

        verify(redisService)
                .increment(
                        anyString(),
                        any(Duration.class)
                );

        verify(verificationCodeService)
                .isRetryAllowed(
                        VerificationCodeType.LOGIN,
                        dto.getUsername()
                );

        verify(verificationCodeService)
                .validateCode(
                        VerificationCodeType.LOGIN,
                        dto.getUsername(),
                        dto.getVerificationCode()
                );

        verify(userMapper)
                .findByUsernameOnly(
                        dto.getUsername()
                );

        verify(verificationCodeService, never())
                .incrementRetryCount(
                        any(),
                        anyString()
                );

        verify(verificationCodeService, never())
                .deleteCode(
                        VerificationCodeType.LOGIN,
                        dto.getUsername()
                );
    }

    @Test
    void login_shouldRejectWhenPasswordIsIncorrect() {
        UserLoginDTO dto = new UserLoginDTO();
        dto.setUsername("sultan_bek");
        dto.setPassword("wrong-password");
        dto.setVerificationCode("123456");

        User user = new User();
        user.setId(1L);
        user.setMerchantId(MERCHANT_ID);
        user.setUsername(dto.getUsername());
        user.setPassword(PasswordUtil.encode("correct-password"));
        user.setStatus(UserStatus.ACTIVE);

        when(redisService.increment(
                anyString(),
                any(Duration.class)
        )).thenReturn(1L);

        when(verificationCodeService.isRetryAllowed(
                VerificationCodeType.LOGIN,
                dto.getUsername()
        )).thenReturn(true);

        when(verificationCodeService.validateCode(
                VerificationCodeType.LOGIN,
                dto.getUsername(),
                dto.getVerificationCode()
        )).thenReturn(true);

        when(userMapper.findByUsernameOnly(
                dto.getUsername()
        )).thenReturn(user);

        assertThatThrownBy(() -> userService.login(dto))
                .satisfies(exception ->
                        assertBusinessException(
                                exception,
                                ErrorCode.USERNAME_OR_PASSWORD_ERROR,
                                ErrorMessage.USERNAME_OR_PASSWORD_ERROR
                        )
                );

        verify(redisService)
                .increment(
                        anyString(),
                        any(Duration.class)
                );

        verify(verificationCodeService)
                .isRetryAllowed(
                        VerificationCodeType.LOGIN,
                        dto.getUsername()
                );

        verify(verificationCodeService)
                .validateCode(
                        VerificationCodeType.LOGIN,
                        dto.getUsername(),
                        dto.getVerificationCode()
                );

        verify(userMapper)
                .findByUsernameOnly(
                        dto.getUsername()
                );

        verify(verificationCodeService, never())
                .incrementRetryCount(
                        any(),
                        anyString()
                );

        verify(verificationCodeService, never())
                .deleteCode(
                        VerificationCodeType.LOGIN,
                        dto.getUsername()
                );
    }

    // ==================== Logout ====================
    @Test
    void logout_shouldBlacklistTokenSuccessfully() {
        String token = "test-jwt-token";
        Duration remainingExpiration = Duration.ofMinutes(30);

        try (MockedStatic<JwtUtil> jwtUtilMock =
                     mockStatic(JwtUtil.class)) {

            jwtUtilMock
                    .when(() -> JwtUtil.getRemainingExpiration(token))
                    .thenReturn(remainingExpiration);

            userService.logout(token);

            jwtUtilMock.verify(
                    () -> JwtUtil.getRemainingExpiration(token)
            );

            verify(jwtBlacklistService)
                    .blacklistToken(
                            token,
                            remainingExpiration
                    );
        }
    }

    // ==================== Get Profile ====================
    @Test
    void getProfile_shouldReturnCachedUser() {
        Long userId = 1L;
        Long merchantId = 1L;

        UserDTO cachedUser = new UserDTO();
        cachedUser.setId(userId);
        cachedUser.setUsername("sultan_bek");
        cachedUser.setNickname("Sultan");
        cachedUser.setPhone("+77001234567");
        cachedUser.setStatus(UserStatus.ACTIVE);

        try (MockedStatic<AuthContext> authContextMock =
                     mockStatic(AuthContext.class)) {

            authContextMock
                    .when(AuthContext::getCurrentUserId)
                    .thenReturn(userId);

            authContextMock
                    .when(AuthContext::getCurrentMerchantId)
                    .thenReturn(merchantId);

            when(redisService.get(
                    anyString(),
                    eq(UserDTO.class)
            )).thenReturn(cachedUser);

            UserDTO result = userService.getProfile();

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(userId);
            assertThat(result.getUsername())
                    .isEqualTo("sultan_bek");
            assertThat(result.getNickname())
                    .isEqualTo("Sultan");
            assertThat(result.getPhone())
                    .isEqualTo("+77001234567");
            assertThat(result.getStatus())
                    .isEqualTo(UserStatus.ACTIVE);

            verify(redisService)
                    .get(
                            anyString(),
                            eq(UserDTO.class)
                    );

            verify(userMapper, never())
                    .findById(
                            anyLong(),
                            anyLong()
                    );

            verify(redisService, never())
                    .set(
                            anyString(),
                            any(),
                            any(Duration.class)
                    );
        }
    }

    @Test
    void getProfile_shouldQueryDatabaseAndCacheWhenCacheMiss() {
        Long userId = 1L;
        Long merchantId = 1L;

        User user = new User();
        user.setId(userId);
        user.setMerchantId(merchantId);
        user.setUsername("sultan_bek");
        user.setNickname("Sultan");
        user.setPhone("+77001234567");
        user.setStatus(UserStatus.ACTIVE);

        try (MockedStatic<AuthContext> authContextMock =
                     mockStatic(AuthContext.class)) {

            authContextMock
                    .when(AuthContext::getCurrentUserId)
                    .thenReturn(userId);

            authContextMock
                    .when(AuthContext::getCurrentMerchantId)
                    .thenReturn(merchantId);

            when(redisService.get(
                    anyString(),
                    eq(UserDTO.class)
            )).thenReturn(null);

            when(userMapper.findById(
                    userId,
                    merchantId
            )).thenReturn(user);

            UserDTO result = userService.getProfile();

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(userId);
            assertThat(result.getUsername())
                    .isEqualTo("sultan_bek");
            assertThat(result.getNickname())
                    .isEqualTo("Sultan");
            assertThat(result.getPhone())
                    .isEqualTo("+77001234567");
            assertThat(result.getStatus())
                    .isEqualTo(UserStatus.ACTIVE);

            verify(redisService)
                    .get(
                            anyString(),
                            eq(UserDTO.class)
                    );

            verify(userMapper)
                    .findById(
                            userId,
                            merchantId
                    );

            verify(redisService)
                    .set(
                            anyString(),
                            any(UserDTO.class),
                            any(Duration.class)
                    );
        }
    }

    @Test
    void getProfile_shouldRejectWhenUserNotFound() {
        Long userId = 999L;
        Long merchantId = 1L;

        try (MockedStatic<AuthContext> authContextMock =
                     mockStatic(AuthContext.class)) {

            authContextMock
                    .when(AuthContext::getCurrentUserId)
                    .thenReturn(userId);

            authContextMock
                    .when(AuthContext::getCurrentMerchantId)
                    .thenReturn(merchantId);

            when(redisService.get(
                    anyString(),
                    eq(UserDTO.class)
            )).thenReturn(null);

            when(userMapper.findById(
                    userId,
                    merchantId
            )).thenReturn(null);

            assertThatThrownBy(() -> userService.getProfile())
                    .satisfies(exception ->
                            assertBusinessException(
                                    exception,
                                    ErrorCode.USER_NOT_FOUND,
                                    ErrorMessage.USER_NOT_FOUND
                            )
                    );

            verify(redisService)
                    .get(
                            anyString(),
                            eq(UserDTO.class)
                    );

            verify(userMapper)
                    .findById(
                            userId,
                            merchantId
                    );

            verify(redisService, never())
                    .set(
                            anyString(),
                            any(),
                            any(Duration.class)
                    );
        }
    }

    // ==================== Get User By ID ====================
    @Test
    void getUserById_shouldReturnCachedUser() {
        Long userId = 1L;

        UserDTO cachedUser = new UserDTO();
        cachedUser.setId(userId);
        cachedUser.setUsername("sultan_bek");
        cachedUser.setNickname("Sultan");
        cachedUser.setPhone("+77001234567");
        cachedUser.setStatus(UserStatus.ACTIVE);

        when(redisService.get(
                anyString(),
                eq(Object.class)
        )).thenReturn(cachedUser);

        UserDTO result = userService.getUserById(userId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(userId);
        assertThat(result.getUsername())
                .isEqualTo("sultan_bek");
        assertThat(result.getNickname())
                .isEqualTo("Sultan");
        assertThat(result.getPhone())
                .isEqualTo("+77001234567");
        assertThat(result.getStatus())
                .isEqualTo(UserStatus.ACTIVE);

        verify(redisService)
                .get(
                        anyString(),
                        eq(Object.class)
                );

        verify(userMapper, never())
                .findByIdOnly(anyLong());

        verify(redisService, never())
                .set(
                        anyString(),
                        any(),
                        any(Duration.class)
                );
    }

    @Test
    void getUserById_shouldRejectWhenEmptyCacheHit() {
        Long userId = 999L;

        when(redisService.get(
                anyString(),
                eq(Object.class)
        )).thenReturn("NULL");

        assertThatThrownBy(() -> userService.getUserById(userId))
                .satisfies(exception ->
                        assertBusinessException(
                                exception,
                                ErrorCode.USER_NOT_FOUND,
                                ErrorMessage.USER_NOT_FOUND
                        )
                );

        verify(redisService)
                .get(
                        anyString(),
                        eq(Object.class)
                );

        verify(userMapper, never())
                .findByIdOnly(anyLong());

        verify(redisService, never())
                .set(
                        anyString(),
                        any(),
                        any(Duration.class)
                );
    }

    @Test
    void getUserById_shouldQueryDatabaseAndCacheWhenCacheMiss() {
        Long userId = 1L;

        User user = new User();
        user.setId(userId);
        user.setMerchantId(MERCHANT_ID);
        user.setUsername("sultan_bek");
        user.setNickname("Sultan");
        user.setPhone("+77001234567");
        user.setStatus(UserStatus.ACTIVE);

        when(redisService.get(
                anyString(),
                eq(Object.class)
        )).thenReturn(null);

        when(userMapper.findByIdOnly(
                userId
        )).thenReturn(user);

        UserDTO result = userService.getUserById(userId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(userId);
        assertThat(result.getUsername())
                .isEqualTo("sultan_bek");
        assertThat(result.getNickname())
                .isEqualTo("Sultan");
        assertThat(result.getPhone())
                .isEqualTo("+77001234567");
        assertThat(result.getStatus())
                .isEqualTo(UserStatus.ACTIVE);

        verify(redisService)
                .get(
                        anyString(),
                        eq(Object.class)
                );

        verify(userMapper)
                .findByIdOnly(userId);

        verify(redisService)
                .set(
                        anyString(),
                        any(UserDTO.class),
                        any(Duration.class)
                );
    }

    @Test
    void getUserById_shouldCacheEmptyResultWhenUserNotFound() {
        Long userId = 999L;

        when(redisService.get(
                anyString(),
                eq(Object.class)
        )).thenReturn(null);

        when(userMapper.findByIdOnly(
                userId
        )).thenReturn(null);

        assertThatThrownBy(() -> userService.getUserById(userId))
                .satisfies(exception ->
                        assertBusinessException(
                                exception,
                                ErrorCode.USER_NOT_FOUND,
                                ErrorMessage.USER_NOT_FOUND
                        )
                );

        verify(redisService)
                .get(
                        anyString(),
                        eq(Object.class)
                );

        verify(userMapper)
                .findByIdOnly(userId);

        verify(redisService)
                .set(
                        anyString(),
                        eq("NULL"),
                        eq(Duration.ofMinutes(5))
                );
    }

    // ==================== Update Profile ====================
    @Test
    void updateProfile_shouldUpdateUserSuccessfully() {
        Long userId = 1L;
        Long merchantId = 1L;

        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setNickname("Updated Sultan");
        dto.setPhone("+77009999999");

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setMerchantId(merchantId);
        existingUser.setUsername("sultan_bek");
        existingUser.setNickname("Sultan");
        existingUser.setPhone("+77001234567");
        existingUser.setStatus(UserStatus.ACTIVE);

        try (MockedStatic<AuthContext> authContextMock =
                     mockStatic(AuthContext.class)) {

            authContextMock
                    .when(AuthContext::getCurrentUserId)
                    .thenReturn(userId);

            authContextMock
                    .when(AuthContext::getCurrentMerchantId)
                    .thenReturn(merchantId);

            when(userMapper.findById(
                    userId,
                    merchantId
            )).thenReturn(existingUser);

            when(userMapper.updateProfile(
                    any(User.class)
            )).thenReturn(1);

            userService.updateProfile(dto);

            verify(userMapper)
                    .findById(
                            userId,
                            merchantId
                    );

            verify(userMapper)
                    .updateProfile(
                            argThat(user ->
                                    userId.equals(user.getId())
                                            && merchantId.equals(user.getMerchantId())
                                            && dto.getNickname().equals(user.getNickname())
                                            && dto.getPhone().equals(user.getPhone())
                            )
                    );

            verify(redisService)
                    .delete(
                            anyString()
                    );
        }
    }

    @Test
    void updateProfile_shouldRejectWhenUserNotFound() {
        Long userId = 999L;
        Long merchantId = 1L;

        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setNickname("Updated Sultan");
        dto.setPhone("+77009999999");

        try (MockedStatic<AuthContext> authContextMock =
                     mockStatic(AuthContext.class)) {

            authContextMock
                    .when(AuthContext::getCurrentUserId)
                    .thenReturn(userId);

            authContextMock
                    .when(AuthContext::getCurrentMerchantId)
                    .thenReturn(merchantId);

            when(userMapper.findById(
                    userId,
                    merchantId
            )).thenReturn(null);

            assertThatThrownBy(() -> userService.updateProfile(dto))
                    .satisfies(exception ->
                            assertBusinessException(
                                    exception,
                                    ErrorCode.USER_NOT_FOUND,
                                    ErrorMessage.USER_NOT_FOUND
                            )
                    );

            verify(userMapper)
                    .findById(
                            userId,
                            merchantId
                    );

            verify(userMapper, never())
                    .updateProfile(any(User.class));

            verify(redisService, never())
                    .delete(anyString());
        }
    }

    @Test
    void updateProfile_shouldRejectWhenUpdateFails() {
        Long userId = 1L;
        Long merchantId = 1L;

        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setNickname("Updated Sultan");
        dto.setPhone("+77009999999");

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setMerchantId(merchantId);
        existingUser.setUsername("sultan_bek");
        existingUser.setNickname("Sultan");
        existingUser.setPhone("+77001234567");
        existingUser.setStatus(UserStatus.ACTIVE);

        try (MockedStatic<AuthContext> authContextMock =
                     mockStatic(AuthContext.class)) {

            authContextMock
                    .when(AuthContext::getCurrentUserId)
                    .thenReturn(userId);

            authContextMock
                    .when(AuthContext::getCurrentMerchantId)
                    .thenReturn(merchantId);

            when(userMapper.findById(
                    userId,
                    merchantId
            )).thenReturn(existingUser);

            when(userMapper.updateProfile(
                    any(User.class)
            )).thenReturn(0);

            assertThatThrownBy(() -> userService.updateProfile(dto))
                    .satisfies(exception ->
                            assertBusinessException(
                                    exception,
                                    ErrorCode.USER_UPDATE_FAILED,
                                    ErrorMessage.USER_UPDATE_FAILED
                            )
                    );

            verify(userMapper)
                    .findById(
                            userId,
                            merchantId
                    );

            verify(userMapper)
                    .updateProfile(
                            argThat(user ->
                                    userId.equals(user.getId())
                                            && merchantId.equals(user.getMerchantId())
                                            && dto.getNickname().equals(user.getNickname())
                                            && dto.getPhone().equals(user.getPhone())
                            )
                    );

            verify(redisService, never())
                    .delete(anyString());
        }
    }

    // ==================== Get My Order ====================
    @Test
    void getMyOrders_shouldReturnPagedOrdersSuccessfully() {
        Long userId = 1L;

        OrderQueryDTO query = new OrderQueryDTO();
        query.setPage(2);
        query.setPageSize(10);
        query.setStatus(OrderStatus.PENDING);

        Order order1 = new Order();
        order1.setId(1L);
        order1.setMerchantId(MERCHANT_ID);
        order1.setUserId(userId);
        order1.setOrderNumber("ORDER-001");
        order1.setTotalAmount(new BigDecimal("35.98"));
        order1.setStatus(OrderStatus.PENDING);
        order1.setCreatedAt(
                LocalDateTime.of(2026, 5, 8, 14, 22, 30)
        );

        Order order2 = new Order();
        order2.setId(2L);
        order2.setMerchantId(MERCHANT_ID);
        order2.setUserId(userId);
        order2.setOrderNumber("ORDER-002");
        order2.setTotalAmount(new BigDecimal("50.00"));
        order2.setStatus(OrderStatus.PENDING);
        order2.setCreatedAt(
                LocalDateTime.of(2026, 5, 8, 15, 30, 0)
        );

        List<Order> orders = List.of(order1, order2);

        try (MockedStatic<AuthContext> authContextMock =
                     mockStatic(AuthContext.class)) {

            authContextMock
                    .when(AuthContext::getCurrentUserId)
                    .thenReturn(userId);

            when(orderMapper.findUserOrders(
                    userId,
                    query.getStatus(),
                    10,
                    10
            )).thenReturn(orders);

            when(orderMapper.countUserOrders(
                    userId,
                    query.getStatus()
            )).thenReturn(25L);

            PageResult<OrderPageDTO> result =
                    userService.getMyOrders(query);

            assertThat(result).isNotNull();
            assertThat(result.getTotal()).isEqualTo(25L);
            assertThat(result.getRecords()).hasSize(2);

            assertThat(result.getRecords().get(0).getId())
                    .isEqualTo(1L);
            assertThat(result.getRecords().get(0).getOrderNumber())
                    .isEqualTo("ORDER-001");
            assertThat(result.getRecords().get(0).getTotalAmount())
                    .isEqualByComparingTo("35.98");
            assertThat(result.getRecords().get(0).getStatus())
                    .isEqualTo(OrderStatus.PENDING);

            assertThat(result.getRecords().get(1).getId())
                    .isEqualTo(2L);
            assertThat(result.getRecords().get(1).getOrderNumber())
                    .isEqualTo("ORDER-002");

            verify(orderMapper)
                    .findUserOrders(
                            userId,
                            query.getStatus(),
                            10,
                            10
                    );

            verify(orderMapper)
                    .countUserOrders(
                            userId,
                            query.getStatus()
                    );
        }
    }

    @Test
    void getMyOrders_shouldReturnAllOrdersWhenStatusIsNull() {
        Long userId = 1L;

        OrderQueryDTO query = new OrderQueryDTO();
        query.setPage(1);
        query.setPageSize(10);
        query.setStatus(null);

        Order pendingOrder = new Order();
        pendingOrder.setId(1L);
        pendingOrder.setMerchantId(MERCHANT_ID);
        pendingOrder.setUserId(userId);
        pendingOrder.setOrderNumber("ORDER-001");
        pendingOrder.setTotalAmount(new BigDecimal("35.98"));
        pendingOrder.setStatus(OrderStatus.PENDING);

        Order paidOrder = new Order();
        paidOrder.setId(2L);
        paidOrder.setMerchantId(MERCHANT_ID);
        paidOrder.setUserId(userId);
        paidOrder.setOrderNumber("ORDER-002");
        paidOrder.setTotalAmount(new BigDecimal("50.00"));
        paidOrder.setStatus(OrderStatus.PAID);

        List<Order> orders = List.of(
                pendingOrder,
                paidOrder
        );

        try (MockedStatic<AuthContext> authContextMock =
                     mockStatic(AuthContext.class)) {

            authContextMock
                    .when(AuthContext::getCurrentUserId)
                    .thenReturn(userId);

            when(orderMapper.findUserOrders(
                    userId,
                    null,
                    0,
                    10
            )).thenReturn(orders);

            when(orderMapper.countUserOrders(
                    userId,
                    null
            )).thenReturn(2L);

            PageResult<OrderPageDTO> result =
                    userService.getMyOrders(query);

            assertThat(result).isNotNull();
            assertThat(result.getTotal()).isEqualTo(2L);
            assertThat(result.getRecords()).hasSize(2);

            assertThat(result.getRecords().get(0).getId())
                    .isEqualTo(1L);
            assertThat(result.getRecords().get(0).getStatus())
                    .isEqualTo(OrderStatus.PENDING);

            assertThat(result.getRecords().get(1).getId())
                    .isEqualTo(2L);
            assertThat(result.getRecords().get(1).getStatus())
                    .isEqualTo(OrderStatus.PAID);

            verify(orderMapper)
                    .findUserOrders(
                            userId,
                            null,
                            0,
                            10
                    );

            verify(orderMapper)
                    .countUserOrders(
                            userId,
                            null
                    );
        }
    }

    @Test
    void getMyOrders_shouldReturnEmptyPageWhenNoOrdersFound() {
        Long userId = 1L;

        OrderQueryDTO query = new OrderQueryDTO();
        query.setPage(1);
        query.setPageSize(10);
        query.setStatus(OrderStatus.PENDING);

        try (MockedStatic<AuthContext> authContextMock =
                     mockStatic(AuthContext.class)) {

            authContextMock
                    .when(AuthContext::getCurrentUserId)
                    .thenReturn(userId);

            when(orderMapper.findUserOrders(
                    userId,
                    query.getStatus(),
                    0,
                    10
            )).thenReturn(List.of());

            when(orderMapper.countUserOrders(
                    userId,
                    query.getStatus()
            )).thenReturn(0L);

            PageResult<OrderPageDTO> result =
                    userService.getMyOrders(query);

            assertThat(result).isNotNull();
            assertThat(result.getTotal()).isZero();
            assertThat(result.getRecords()).isEmpty();

            verify(orderMapper)
                    .findUserOrders(
                            userId,
                            query.getStatus(),
                            0,
                            10
                    );

            verify(orderMapper)
                    .countUserOrders(
                            userId,
                            query.getStatus()
                    );
        }
    }

    // ==================== Cancel Order ====================
    @Test
    void cancelOrder_shouldCancelPendingOrderSuccessfully() {
        Long userId = 1L;
        Long orderId = 100L;
        Long merchantId = 1L;

        Order order = new Order();
        order.setId(orderId);
        order.setMerchantId(merchantId);
        order.setUserId(userId);
        order.setOrderNumber("ORDER-001");
        order.setTotalAmount(new BigDecimal("35.98"));
        order.setStatus(OrderStatus.PENDING);

        try (MockedStatic<AuthContext> authContextMock =
                     mockStatic(AuthContext.class)) {

            authContextMock
                    .when(AuthContext::getCurrentUserId)
                    .thenReturn(userId);

            when(orderMapper.findUserOrderById(
                    orderId,
                    userId
            )).thenReturn(order);

            when(orderMapper.updateStatus(
                    orderId,
                    merchantId,
                    OrderStatus.CANCELLED
            )).thenReturn(1);

            userService.cancelOrder(orderId);

            verify(orderMapper)
                    .findUserOrderById(
                            orderId,
                            userId
                    );

            verify(orderMapper)
                    .updateStatus(
                            orderId,
                            merchantId,
                            OrderStatus.CANCELLED
                    );
        }
    }

    @Test
    void cancelOrder_shouldRejectWhenOrderNotFound() {
        Long userId = 1L;
        Long orderId = 999L;

        try (MockedStatic<AuthContext> authContextMock =
                     mockStatic(AuthContext.class)) {

            authContextMock
                    .when(AuthContext::getCurrentUserId)
                    .thenReturn(userId);

            when(orderMapper.findUserOrderById(
                    orderId,
                    userId
            )).thenReturn(null);

            assertThatThrownBy(() -> userService.cancelOrder(orderId))
                    .satisfies(exception ->
                            assertBusinessException(
                                    exception,
                                    ErrorCode.ORDER_NOT_FOUND,
                                    ErrorMessage.ORDER_NOT_FOUND
                            )
                    );

            verify(orderMapper)
                    .findUserOrderById(
                            orderId,
                            userId
                    );

            verify(orderMapper, never())
                    .updateStatus(
                            anyLong(),
                            anyLong(),
                            anyInt()
                    );
        }
    }

    @Test
    void cancelOrder_shouldRejectWhenOrderCannotBeCancelled() {
        Long userId = 1L;
        Long orderId = 100L;
        Long merchantId = 1L;

        Order order = new Order();
        order.setId(orderId);
        order.setMerchantId(merchantId);
        order.setUserId(userId);
        order.setOrderNumber("ORDER-001");
        order.setTotalAmount(new BigDecimal("35.98"));
        order.setStatus(OrderStatus.PAID);

        try (MockedStatic<AuthContext> authContextMock =
                     mockStatic(AuthContext.class)) {

            authContextMock
                    .when(AuthContext::getCurrentUserId)
                    .thenReturn(userId);

            when(orderMapper.findUserOrderById(
                    orderId,
                    userId
            )).thenReturn(order);

            assertThatThrownBy(() -> userService.cancelOrder(orderId))
                    .satisfies(exception ->
                            assertBusinessException(
                                    exception,
                                    ErrorCode.ORDER_CANNOT_BE_CANCELLED,
                                    ErrorMessage.ORDER_CANNOT_BE_CANCELLED
                            )
                    );

            verify(orderMapper)
                    .findUserOrderById(
                            orderId,
                            userId
                    );

            verify(orderMapper, never())
                    .updateStatus(
                            anyLong(),
                            anyLong(),
                            anyInt()
                    );
        }
    }

    @Test
    void cancelOrder_shouldRejectWhenOrderUpdateFails() {
        Long userId = 1L;
        Long orderId = 100L;
        Long merchantId = 1L;

        Order order = new Order();
        order.setId(orderId);
        order.setMerchantId(merchantId);
        order.setUserId(userId);
        order.setOrderNumber("ORDER-001");
        order.setTotalAmount(new BigDecimal("35.98"));
        order.setStatus(OrderStatus.PENDING);

        try (MockedStatic<AuthContext> authContextMock =
                     mockStatic(AuthContext.class)) {

            authContextMock
                    .when(AuthContext::getCurrentUserId)
                    .thenReturn(userId);

            when(orderMapper.findUserOrderById(
                    orderId,
                    userId
            )).thenReturn(order);

            when(orderMapper.updateStatus(
                    orderId,
                    merchantId,
                    OrderStatus.CANCELLED
            )).thenReturn(0);

            assertThatThrownBy(() -> userService.cancelOrder(orderId))
                    .satisfies(exception ->
                            assertBusinessException(
                                    exception,
                                    ErrorCode.ORDER_UPDATE_FAILED,
                                    ErrorMessage.ORDER_UPDATE_FAILED
                            )
                    );

            verify(orderMapper)
                    .findUserOrderById(
                            orderId,
                            userId
                    );

            verify(orderMapper)
                    .updateStatus(
                            orderId,
                            merchantId,
                            OrderStatus.CANCELLED
                    );
        }
    }

    // ==================== Pay Order ====================
    @Test
    void payOrder_shouldPayPendingOrderSuccessfully() {
        Long userId = 1L;
        Long orderId = 100L;
        Long merchantId = 1L;

        Order order = new Order();
        order.setId(orderId);
        order.setMerchantId(merchantId);
        order.setUserId(userId);
        order.setOrderNumber("ORDER-001");
        order.setTotalAmount(new BigDecimal("35.98"));
        order.setStatus(OrderStatus.PENDING);

        try (MockedStatic<AuthContext> authContextMock =
                     mockStatic(AuthContext.class)) {

            authContextMock
                    .when(AuthContext::getCurrentUserId)
                    .thenReturn(userId);

            when(orderMapper.findUserOrderById(
                    orderId,
                    userId
            )).thenReturn(order);

            when(orderMapper.updateStatus(
                    orderId,
                    merchantId,
                    OrderStatus.PAID
            )).thenReturn(1);

            userService.payOrder(orderId);

            verify(orderMapper)
                    .findUserOrderById(
                            orderId,
                            userId
                    );

            verify(orderMapper)
                    .updateStatus(
                            orderId,
                            merchantId,
                            OrderStatus.PAID
                    );
        }
    }

    @Test
    void payOrder_shouldRejectWhenOrderNotFound() {
        Long userId = 1L;
        Long orderId = 999L;

        try (MockedStatic<AuthContext> authContextMock =
                     mockStatic(AuthContext.class)) {

            authContextMock
                    .when(AuthContext::getCurrentUserId)
                    .thenReturn(userId);

            when(orderMapper.findUserOrderById(
                    orderId,
                    userId
            )).thenReturn(null);

            assertThatThrownBy(() -> userService.payOrder(orderId))
                    .satisfies(exception ->
                            assertBusinessException(
                                    exception,
                                    ErrorCode.ORDER_NOT_FOUND,
                                    ErrorMessage.ORDER_NOT_FOUND
                            )
                    );

            verify(orderMapper)
                    .findUserOrderById(
                            orderId,
                            userId
                    );

            verify(orderMapper, never())
                    .updateStatus(
                            anyLong(),
                            anyLong(),
                            anyInt()
                    );
        }
    }

    @Test
    void payOrder_shouldRejectWhenOrderCannotBePaid() {
        Long userId = 1L;
        Long orderId = 100L;
        Long merchantId = 1L;

        Order order = new Order();
        order.setId(orderId);
        order.setMerchantId(merchantId);
        order.setUserId(userId);
        order.setOrderNumber("ORDER-001");
        order.setTotalAmount(new BigDecimal("35.98"));
        order.setStatus(OrderStatus.PAID);

        try (MockedStatic<AuthContext> authContextMock =
                     mockStatic(AuthContext.class)) {

            authContextMock
                    .when(AuthContext::getCurrentUserId)
                    .thenReturn(userId);

            when(orderMapper.findUserOrderById(
                    orderId,
                    userId
            )).thenReturn(order);

            assertThatThrownBy(() -> userService.payOrder(orderId))
                    .satisfies(exception ->
                            assertBusinessException(
                                    exception,
                                    ErrorCode.ORDER_CANNOT_BE_PAID,
                                    ErrorMessage.ORDER_CANNOT_BE_PAID
                            )
                    );

            verify(orderMapper)
                    .findUserOrderById(
                            orderId,
                            userId
                    );

            verify(orderMapper, never())
                    .updateStatus(
                            anyLong(),
                            anyLong(),
                            anyInt()
                    );
        }
    }

    @Test
    void payOrder_shouldRejectWhenOrderUpdateFails() {
        Long userId = 1L;
        Long orderId = 100L;
        Long merchantId = 1L;

        Order order = new Order();
        order.setId(orderId);
        order.setMerchantId(merchantId);
        order.setUserId(userId);
        order.setOrderNumber("ORDER-001");
        order.setTotalAmount(new BigDecimal("35.98"));
        order.setStatus(OrderStatus.PENDING);

        try (MockedStatic<AuthContext> authContextMock =
                     mockStatic(AuthContext.class)) {

            authContextMock
                    .when(AuthContext::getCurrentUserId)
                    .thenReturn(userId);

            when(orderMapper.findUserOrderById(
                    orderId,
                    userId
            )).thenReturn(order);

            when(orderMapper.updateStatus(
                    orderId,
                    merchantId,
                    OrderStatus.PAID
            )).thenReturn(0);

            assertThatThrownBy(() -> userService.payOrder(orderId))
                    .satisfies(exception ->
                            assertBusinessException(
                                    exception,
                                    ErrorCode.ORDER_UPDATE_FAILED,
                                    ErrorMessage.ORDER_UPDATE_FAILED
                            )
                    );

            verify(orderMapper)
                    .findUserOrderById(
                            orderId,
                            userId
                    );

            verify(orderMapper)
                    .updateStatus(
                            orderId,
                            merchantId,
                            OrderStatus.PAID
                    );
        }
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