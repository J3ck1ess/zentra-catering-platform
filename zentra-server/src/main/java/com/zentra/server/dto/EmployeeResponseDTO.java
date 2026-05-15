package com.zentra.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Employee API response
 */
@Schema(description = "Employee API response")
public class EmployeeResponseDTO extends BaseResponseDTO{

    @Schema(description = "Employee response data")
    private EmployeeDTO data;

    // Getter and Setter
    public EmployeeDTO getData() {
        return data;
    }

    public void setData(EmployeeDTO data) {
        this.data = data;
    }
}
