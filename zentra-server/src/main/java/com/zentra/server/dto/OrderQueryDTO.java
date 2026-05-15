package com.zentra.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;

/**
 * Query object for orders
 */
@Schema(description = "Order query request")
public class OrderQueryDTO {

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
     * Optional order status filter
     */
    @Schema(description = "Order status filter (1 = Pending, 2 = Confirmed, 3 = Completed, 4 = Cancelled", example = "1")
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
