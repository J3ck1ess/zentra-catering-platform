package com.zentra.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Dashboard API response
 */
@Schema(description = "Dashboard API response")
public class DashboardResponseDTO extends BaseResponseDTO {

    @Schema(description = "Dashboard response data")
    private DashboardDTO data;

    // Getter and Setter

    public DashboardDTO getData() {
        return data;
    }

    public void setData(DashboardDTO data) {
        this.data = data;
    }

}