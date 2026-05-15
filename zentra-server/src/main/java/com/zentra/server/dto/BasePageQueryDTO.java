package com.zentra.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;

/**
 * Base pagination query DTO
 */
@Schema(description = "Base pagination query request")
public class BasePageQueryDTO {

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
}
