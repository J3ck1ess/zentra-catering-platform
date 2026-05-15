package com.zentra.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Employee pagination API response
 */
@Schema(description = "Employee pagination API response")
public class EmployeePageResponseDTO extends BaseResponseDTO{

    @Schema(description = "Employee pagination data")
    private PageDataDTO<EmployeeDTO> data;

    // Getter and Setter
    public PageDataDTO<EmployeeDTO> getData() {
        return data;
    }

    public void setData(PageDataDTO<EmployeeDTO> data) {
        this.data = data;
    }
}
