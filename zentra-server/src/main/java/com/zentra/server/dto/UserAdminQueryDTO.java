package com.zentra.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Query DTO for admin user pagination
 */
@Schema(description = "Admin user query request")
public class UserAdminQueryDTO extends BasePageQueryDTO{

    /**
     * Optional username filter
     */
    @Schema(description = "Username filter", example = "sultan")
    private String username;

    /**
     * Optional status filter
     */
    @Schema(description = "User status filter (1 = Active, 0 = Disabled)", example = "1")
    private Integer status;

    // Getter and Setter
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
