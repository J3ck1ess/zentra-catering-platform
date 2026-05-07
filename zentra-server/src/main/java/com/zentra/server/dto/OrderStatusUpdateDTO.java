package com.zentra.server.dto;

import jakarta.validation.constraints.NotNull;

/**
 * DTO for updating order status
 */
public class OrderStatusUpdateDTO {

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
