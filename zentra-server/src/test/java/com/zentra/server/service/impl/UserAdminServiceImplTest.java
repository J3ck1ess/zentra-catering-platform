package com.zentra.server.service.impl;

import com.zentra.common.constant.ErrorCode;
import com.zentra.common.constant.ErrorMessage;
import com.zentra.common.constant.UserStatus;
import com.zentra.common.exception.BusinessException;
import com.zentra.common.result.PageResult;
import com.zentra.server.dto.UserAdminDTO;
import com.zentra.server.dto.UserAdminQueryDTO;
import com.zentra.server.entity.User;
import com.zentra.server.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserAdminServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserAdminServiceImpl userAdminService;

    // ==================== Page ====================
    @Test
    void page_shouldReturnPagedUsersSuccessfully() {
        UserAdminQueryDTO query = new UserAdminQueryDTO();
        query.setPage(2);
        query.setPageSize(10);
        query.setUsername("sultan");
        query.setStatus(UserStatus.ACTIVE);

        LocalDateTime createdAt1 =
                LocalDateTime.of(2026, 5, 7, 18, 50, 56);

        LocalDateTime createdAt2 =
                LocalDateTime.of(2026, 5, 6, 12, 30, 0);

        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("sultan");
        user1.setStatus(UserStatus.ACTIVE);
        user1.setCreatedAt(createdAt1);

        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("sultan_test");
        user2.setStatus(UserStatus.ACTIVE);
        user2.setCreatedAt(createdAt2);

        List<User> users = List.of(user1, user2);

        when(userMapper.findPage(
                "sultan",
                UserStatus.ACTIVE,
                10,
                10
        )).thenReturn(users);

        when(userMapper.count(
                "sultan",
                UserStatus.ACTIVE
        )).thenReturn(25L);

        PageResult<UserAdminDTO> result =
                userAdminService.page(query);

        assertThat(result).isNotNull();
        assertThat(result.getTotal()).isEqualTo(25L);
        assertThat(result.getRecords()).hasSize(2);

        assertThat(result.getRecords().get(0).getId())
                .isEqualTo(1L);
        assertThat(result.getRecords().get(0).getUsername())
                .isEqualTo("sultan");
        assertThat(result.getRecords().get(0).getStatus())
                .isEqualTo(UserStatus.ACTIVE);
        assertThat(result.getRecords().get(0).getCreatedAt())
                .isEqualTo(createdAt1);

        assertThat(result.getRecords().get(1).getId())
                .isEqualTo(2L);
        assertThat(result.getRecords().get(1).getUsername())
                .isEqualTo("sultan_test");
        assertThat(result.getRecords().get(1).getStatus())
                .isEqualTo(UserStatus.ACTIVE);
        assertThat(result.getRecords().get(1).getCreatedAt())
                .isEqualTo(createdAt2);

        verify(userMapper)
                .findPage(
                        "sultan",
                        UserStatus.ACTIVE,
                        10,
                        10
                );

        verify(userMapper)
                .count(
                        "sultan",
                        UserStatus.ACTIVE
                );
    }

    @Test
    void page_shouldReturnAllUsersWhenFiltersAreNull() {
        UserAdminQueryDTO query = new UserAdminQueryDTO();
        query.setPage(1);
        query.setPageSize(10);
        query.setUsername(null);
        query.setStatus(null);

        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("sultan");
        user1.setStatus(UserStatus.ACTIVE);

        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("test_user");
        user2.setStatus(UserStatus.DISABLED);

        List<User> users = List.of(user1, user2);

        when(userMapper.findPage(
                null,
                null,
                0,
                10
        )).thenReturn(users);

        when(userMapper.count(
                null,
                null
        )).thenReturn(2L);

        PageResult<UserAdminDTO> result =
                userAdminService.page(query);

        assertThat(result).isNotNull();
        assertThat(result.getTotal()).isEqualTo(2L);
        assertThat(result.getRecords()).hasSize(2);

        assertThat(result.getRecords().get(0).getId())
                .isEqualTo(1L);
        assertThat(result.getRecords().get(0).getUsername())
                .isEqualTo("sultan");
        assertThat(result.getRecords().get(0).getStatus())
                .isEqualTo(UserStatus.ACTIVE);

        assertThat(result.getRecords().get(1).getId())
                .isEqualTo(2L);
        assertThat(result.getRecords().get(1).getUsername())
                .isEqualTo("test_user");
        assertThat(result.getRecords().get(1).getStatus())
                .isEqualTo(UserStatus.DISABLED);

        verify(userMapper)
                .findPage(
                        null,
                        null,
                        0,
                        10
                );

        verify(userMapper)
                .count(
                        null,
                        null
                );
    }

    @Test
    void page_shouldReturnEmptyPageWhenNoUsersFound() {
        UserAdminQueryDTO query = new UserAdminQueryDTO();
        query.setPage(1);
        query.setPageSize(10);
        query.setUsername("unknown");
        query.setStatus(UserStatus.ACTIVE);

        when(userMapper.findPage(
                "unknown",
                UserStatus.ACTIVE,
                0,
                10
        )).thenReturn(List.of());

        when(userMapper.count(
                "unknown",
                UserStatus.ACTIVE
        )).thenReturn(0L);

        PageResult<UserAdminDTO> result =
                userAdminService.page(query);

        assertThat(result).isNotNull();
        assertThat(result.getTotal()).isZero();
        assertThat(result.getRecords()).isEmpty();

        verify(userMapper)
                .findPage(
                        "unknown",
                        UserStatus.ACTIVE,
                        0,
                        10
                );

        verify(userMapper)
                .count(
                        "unknown",
                        UserStatus.ACTIVE
                );
    }

    // ==================== Update ====================
    @Test
    void updateStatus_shouldUpdateUserStatusSuccessfully() {
        Long userId = 1L;

        User user = new User();
        user.setId(userId);
        user.setMerchantId(1L);
        user.setUsername("sultan");
        user.setStatus(UserStatus.ACTIVE);

        when(userMapper.findByIdOnly(userId))
                .thenReturn(user);

        when(userMapper.update(any(User.class)))
                .thenReturn(1);

        userAdminService.updateStatus(
                userId,
                UserStatus.DISABLED
        );

        verify(userMapper)
                .findByIdOnly(userId);

        verify(userMapper)
                .update(
                        argThat(updatedUser ->
                                userId.equals(updatedUser.getId())
                                        && updatedUser.getStatus() ==
                                        UserStatus.DISABLED
                        )
                );
    }

    @Test
    void updateStatus_shouldRejectWhenStatusIsInvalid() {
        Long userId = 1L;
        Integer invalidStatus = 99;

        assertThatThrownBy(() ->
                userAdminService.updateStatus(
                        userId,
                        invalidStatus
                )
        ).satisfies(exception ->
                assertBusinessException(
                        exception,
                        ErrorCode.USER_STATUS_INVALID,
                        ErrorMessage.USER_STATUS_INVALID
                )
        );

        verify(userMapper, never())
                .findByIdOnly(anyLong());

        verify(userMapper, never())
                .update(any(User.class));
    }

    @Test
    void updateStatus_shouldRejectWhenUserNotFound() {
        Long userId = 999L;

        when(userMapper.findByIdOnly(userId))
                .thenReturn(null);

        assertThatThrownBy(() ->
                userAdminService.updateStatus(
                        userId,
                        UserStatus.DISABLED
                )
        ).satisfies(exception ->
                assertBusinessException(
                        exception,
                        ErrorCode.USER_NOT_FOUND,
                        ErrorMessage.USER_NOT_FOUND
                )
        );

        verify(userMapper)
                .findByIdOnly(userId);

        verify(userMapper, never())
                .update(any(User.class));
    }

    @Test
    void updateStatus_shouldRejectWhenUpdateFails() {
        Long userId = 1L;

        User user = new User();
        user.setId(userId);
        user.setMerchantId(1L);
        user.setUsername("sultan");
        user.setStatus(UserStatus.ACTIVE);

        when(userMapper.findByIdOnly(userId))
                .thenReturn(user);

        when(userMapper.update(any(User.class)))
                .thenReturn(0);

        assertThatThrownBy(() ->
                userAdminService.updateStatus(
                        userId,
                        UserStatus.DISABLED
                )
        ).satisfies(exception ->
                assertBusinessException(
                        exception,
                        ErrorCode.USER_STATUS_UPDATE_FAILED,
                        ErrorMessage.USER_STATUS_UPDATE_FAILED
                )
        );

        verify(userMapper)
                .findByIdOnly(userId);

        verify(userMapper)
                .update(
                        argThat(updatedUser ->
                                userId.equals(updatedUser.getId())
                                        && updatedUser.getStatus() ==
                                        UserStatus.DISABLED
                        )
                );
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