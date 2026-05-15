package com.zentra.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for updating order status
 */
@Schema(description = "Order status update request")
public class OrderStatusUpdateDTO {

    @Schema(description = "Order status (1 = Pending, 2 = Confirmed, 3 = Completed, 4 = Cancelled)", example = "2")
    @NotNull(message = "status cannot be null")
    private Integer status;

    // Getter and Setter
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
