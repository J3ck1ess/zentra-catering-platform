package com.zentra.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO for paginated admin user response
 */
@Schema(description = "Admin user pagination API response")
public class UserAdminPageResponseDTO extends BaseResponseDTO {

    @Schema(description = "Admin user pagination data")
    private PageDataDTO<UserAdminDTO> data;

    // Getter and Setter
    public PageDataDTO<UserAdminDTO> getData() {
        return data;
    }

    public void setData(PageDataDTO<UserAdminDTO> data) {
        this.data = data;
    }
}
