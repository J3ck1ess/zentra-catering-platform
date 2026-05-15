package com.zentra.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;

/**
 * Query DTO for employee list
 */
@Schema(description = "Employee query request")
public class EmployeeQueryDTO {

    /**
     * Page number (must be >= 1)
     */
    @Schema(description = "Page number", example = "1")
    @Min(value = 1, message = "page must be greater than or equal to 1")
    private Integer page = 1;

    /**
     * Page size (must be >= 1)
     */
    @Schema(description = "Page size", example = "10")
    @Min(value = 1, message = "pageSize must be greater than or equal to 1")
    private Integer pageSize = 10;

    /**
     * Optional username filter
     */
    @Schema(description = "Employee username filter", example = "admin")
    private String username;

    /**
     * Optional status filter
     */
    @Schema(description = "Employee status filter (1 = Active, 0 = Disabled)", example = "1")
    private Integer status;

    // Getter and Setter
    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

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
