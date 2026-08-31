package com.zentra.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zentra.common.constant.ErrorCode;
import com.zentra.common.constant.ErrorMessage;
import com.zentra.common.exception.BusinessException;
import com.zentra.common.result.PageResult;
import com.zentra.server.dto.UserAdminDTO;
import com.zentra.server.dto.UserAdminQueryDTO;
import com.zentra.server.dto.UserStatusUpdateDTO;
import com.zentra.server.exception.GlobalExceptionHandler;
import com.zentra.server.service.UserAdminService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserAdminControllerTest {

    @Mock
    private UserAdminService userAdminService;

    @InjectMocks
    private UserAdminController userAdminController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {

        mockMvc = MockMvcBuilders
                .standaloneSetup(userAdminController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
    }

    // ==================== Page ====================
    @Test
    void page_shouldReturnUsersSuccessfully()
            throws Exception {

        UserAdminDTO user = new UserAdminDTO();
        user.setId(1L);
        user.setUsername("sultan_bek");
        user.setStatus(1);

        PageResult<UserAdminDTO> pageResult =
                new PageResult<>(
                        1L,
                        List.of(user)
                );

        when(userAdminService.page(any(UserAdminQueryDTO.class)))
                .thenReturn(pageResult);

        mockMvc.perform(
                        get("/admin/users")
                                .param("page", "1")
                                .param("pageSize", "10")
                                .param("username", "sultan")
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
                        .value(1))
                .andExpect(jsonPath("$.data.records[0].username")
                        .value("sultan_bek"))
                .andExpect(jsonPath("$.data.records[0].status")
                        .value(1));

        ArgumentCaptor<UserAdminQueryDTO> captor =
                ArgumentCaptor.forClass(UserAdminQueryDTO.class);

        verify(userAdminService)
                .page(captor.capture());

        UserAdminQueryDTO captured = captor.getValue();

        assertThat(captured.getPage())
                .isEqualTo(1);
        assertThat(captured.getPageSize())
                .isEqualTo(10);
        assertThat(captured.getUsername())
                .isEqualTo("sultan");
        assertThat(captured.getStatus())
                .isEqualTo(1);
    }

    // ==================== Update ====================
    @Test
    void updateStatus_shouldUpdateUserStatusSuccessfully()
            throws Exception {

        Long userId = 1L;

        UserStatusUpdateDTO request = new UserStatusUpdateDTO();
        request.setStatus(1);

        doNothing().when(userAdminService)
                .updateStatus(userId, 1);

        mockMvc.perform(
                        patch("/admin/users/{id}/status", userId)
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

        verify(userAdminService)
                .updateStatus(userId, 1);
    }

    @Test
    void updateStatus_shouldRejectRequestWithNullStatus()
            throws Exception {

        UserStatusUpdateDTO request = new UserStatusUpdateDTO();

        mockMvc.perform(
                        patch("/admin/users/{id}/status", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code")
                        .value(ErrorCode.FAILURE))
                .andExpect(jsonPath("$.msg")
                        .value("Status cannot be null"));

        verifyNoInteractions(userAdminService);
    }

    @Test
    void updateStatus_shouldReturnBadRequestWhenStatusIsInvalid()
            throws Exception {

        Long userId = 1L;

        UserStatusUpdateDTO request = new UserStatusUpdateDTO();
        request.setStatus(999);

        doThrow(new BusinessException(
                ErrorCode.USER_STATUS_INVALID,
                ErrorMessage.USER_STATUS_INVALID
        )).when(userAdminService)
                .updateStatus(userId, 999);

        mockMvc.perform(
                        patch("/admin/users/{id}/status", userId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code")
                        .value(ErrorCode.USER_STATUS_INVALID))
                .andExpect(jsonPath("$.msg")
                        .value(ErrorMessage.USER_STATUS_INVALID));

        verify(userAdminService)
                .updateStatus(userId, 999);
    }
}