package com.zentra.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for updating employee status
 */
@Schema(description = "Employee status update request")
public class EmployeeStatusDTO {

    @Schema(description = "Employee ID", example = "1")
    @NotNull(message = "employee id cannot be null")
    private Long id;

    @Schema(
            description = "Employee status (1 = Active, 0 = Disabled)",
            example = "1"
    )
    @NotNull(message = "status cannot be null")
    private Integer status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}