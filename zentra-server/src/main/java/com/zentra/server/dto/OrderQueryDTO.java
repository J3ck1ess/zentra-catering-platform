package com.zentra.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;

/**
 * Query object for orders
 */
@Schema(description = "Order query request")
public class OrderQueryDTO extends BasePageQueryDTO{

    /**
     * Optional order status filter
     */
    @Schema(description = "Order status filter (1 = Pending, 2 = Confirmed, 3 = Completed, 4 = Cancelled", example = "1")
    private Integer status;

    // Getter and Setter
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
