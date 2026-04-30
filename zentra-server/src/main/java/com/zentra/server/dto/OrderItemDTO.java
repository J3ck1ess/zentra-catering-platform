package com.zentra.server.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class OrderItemDTO {

    @NotNull(message = "dish id cannot be null")
    private Long dishId;

    @NotNull(message = "quantity cannot be null")
    @Min(value = 1, message = "quantity must be greater than 0 or equal to 1")
    private Integer quantity;

    // Getter and Setter
    public Long getDishId() {
        return dishId;
    }

    public void setDishId(Long dishId) {
        this.dishId = dishId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
