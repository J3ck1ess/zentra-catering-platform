package com.zentra.server.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Query object for orders
 */
public class OrderQueryDTO {

    /**
     * Page number (must be >= 1)
     */
    @Min(value = 1, message = "page must be greater than or equal to 1")
    private Integer page = 1;

    /**
     * Page size (must be >= 1)
     */
    @Min(value = 1, message = "pageSize must be greater than or equal to 1")
    private Integer pageSize = 10;

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
