package com.zentra.server.service.impl;

import com.zentra.common.auth.AuthInfo;
import com.zentra.common.constant.*;
import com.zentra.common.context.AuthContext;
import com.zentra.common.exception.BusinessException;
import com.zentra.common.result.PageResult;
import com.zentra.common.util.AssertUtil;
import com.zentra.common.util.PasswordUtil;
import com.zentra.server.dto.*;
import com.zentra.server.entity.Order;
import com.zentra.server.entity.User;
import com.zentra.server.mapper.OrderMapper;
import com.zentra.server.mapper.UserMapper;
import com.zentra.server.service.RedisService;
import com.zentra.server.service.UserService;
import com.zentra.common.util.JwtUtil;
import com.zentra.server.service.VerificationCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service implementation for user logic
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    private final OrderMapper orderMapper;

    private static final long MAX_LOGIN_REQUEST_COUNT = 10L;

    /**
     * Verification code service
     */
    private final VerificationCodeService verificationCodeService;

    /**
     * Redis service
     */
    private final RedisService redisService;

    /**
     * Build login rate limit redis key
     */
    private String buildLoginRateLimitKey(String username) {

        return RedisKeyConstants.LOGIN_RATE_LIMIT + username;
    }

    /**
     * Register user
     */
    @Override
    public void register(UserRegisterDTO dto) {

        /**
         * Temporary fixed merchant ID
         *
         * TODO: Replace with dynamic tenant binding in future
         */
        Long merchantId = 1L;

        // Check username duplication
        User existUser = userMapper.findByUsername(
                dto.getUsername(),
                merchantId
        );

        AssertUtil.isNull(
                existUser,
                ErrorCode.USERNAME_ALREADY_EXISTS,
                ErrorMessage.USERNAME_ALREADY_EXISTS
        );

        // Convert DTO -> Entity
        User user = new User();
        BeanUtils.copyProperties(dto, user);

        // Set default properties
        user.setMerchantId(merchantId);
        user.setStatus(UserStatus.ACTIVE);
        user.setPassword(PasswordUtil.encode(dto.getPassword()));

        // Insert user
        int rows = userMapper.insert(user);
        AssertUtil.checkRows(
                rows,
                ErrorCode.USER_REGISTER_FAILED,
                ErrorMessage.USER_REGISTER_FAILED
        );
    }

    /**
     * User login
     */
    @Override
    public LoginResponse login(UserLoginDTO dto) {

        // Increment login request count
        Long loginRequestCount =
                redisService.increment(
                        buildLoginRateLimitKey(dto.getUsername()),
                        RedisTtlConstants.LOGIN_RATE_LIMIT_TTL
                );

        // Check login rate limit
        AssertUtil.isTrue(
                loginRequestCount <= MAX_LOGIN_REQUEST_COUNT,
                ErrorCode.TOO_MANY_REQUESTS,
                ErrorMessage.TOO_MANY_LOGIN_REQUESTS
        );

        // Check verification retry limit
        boolean retryAllowed =
                verificationCodeService
                        .isRetryAllowed(
                                VerificationCodeType.LOGIN,
                                dto.getUsername()
                        );
        AssertUtil.isTrue(
                retryAllowed,
                ErrorCode.BAD_REQUEST,
                ErrorMessage.TOO_MANY_VERIFICATION_ATTEMPTS
        );

        // Validate verification code
        boolean valid =
                verificationCodeService.validateCode(
                        VerificationCodeType.LOGIN,
                        dto.getUsername(),
                        dto.getVerificationCode()
                );

        if (!valid) {

            verificationCodeService
                    .incrementRetryCount(
                            VerificationCodeType.LOGIN,
                            dto.getUsername()
                    );
        }
        AssertUtil.isTrue(
                valid,
                ErrorCode.BAD_REQUEST,
                ErrorMessage.INVALID_VERIFICATION_CODE
        );

        // Query user
        User user = userMapper.findByUsernameOnly(
                dto.getUsername()
        );
        AssertUtil.notNull(
                user,
                ErrorCode.USERNAME_OR_PASSWORD_ERROR,
                ErrorMessage.USERNAME_OR_PASSWORD_ERROR
        );

        // Check user status
        if (user.getStatus().equals(UserStatus.DISABLED)) {

            throw new BusinessException(
                    ErrorCode.USER_DISABLED,
                    ErrorMessage.USER_DISABLED
            );
        }

        // Verify password
        if (!PasswordUtil.matches(
                dto.getPassword(),
                user.getPassword()
        )) {

            throw new BusinessException(
                    ErrorCode.USERNAME_OR_PASSWORD_ERROR,
                    ErrorMessage.USERNAME_OR_PASSWORD_ERROR
            );
        }

        // Delete verification code
        verificationCodeService.deleteCode(
                VerificationCodeType.LOGIN,
                dto.getUsername()
        );

        // Generate JWT token
        AuthInfo authInfo = new AuthInfo(
                user.getId(),
                user.getMerchantId(),
                UserType.USER,
                null
        );

        String token = JwtUtil.generateToken(authInfo);

        return new LoginResponse(token, user.getId());
    }

    /**
     * Get user profile
     */
    @Override
    public UserDTO getProfile() {

        // Current Logged-in user
        Long userId = AuthContext.getCurrentUserId();

        // Get merchant ID
        Long merchantId = AuthContext.getCurrentMerchantId();

        // Query user
        User user = userMapper.findById(
                userId,
                merchantId
        );
        AssertUtil.notNull(
                user,
                ErrorCode.USER_NOT_FOUND,
                ErrorMessage.USER_NOT_FOUND
        );

        // Convert Entity -> DTO
        UserDTO dto = new UserDTO();
        BeanUtils.copyProperties(user, dto);

        return dto;
    }

    /**
     * Get user orders
     */
    @Override
    public PageResult<OrderPageDTO> getMyOrders(OrderQueryDTO query) {

        Long userId = AuthContext.getCurrentUserId();

        Integer page = query.getPage();
        Integer pageSize = query.getPageSize();
        int offset = (page - 1) * pageSize;

        List<Order> list = orderMapper.findUserOrders(
                userId,
                query.getStatus(),
                offset,
                pageSize
        );

        List<OrderPageDTO> records = list.stream().map(order -> {

            OrderPageDTO dto = new OrderPageDTO();
            BeanUtils.copyProperties(order, dto);

            return dto;
        }).toList();

        Long total = orderMapper.countUserOrders(
                userId,
                query.getStatus()
        );

        return new PageResult<>(total, records);

    }
}
