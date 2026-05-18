package com.zentra.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for updating user status
 */
@Schema(description = "User status update request")
public class UserStatusUpdateDTO {

    @Schema(description = "User status (1 = Active, 0 = Disabled)", example = "1")
    @NotNull(message = "Status cannot be null")
    private Integer status;

    // Getter and Setter
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
