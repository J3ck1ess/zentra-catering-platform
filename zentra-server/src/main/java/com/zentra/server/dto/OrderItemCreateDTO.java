package com.zentra.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for creating order item
 */
@Schema(description = "Order item creation request")
public class OrderItemCreateDTO {

    @Schema(description = "Dish ID", example = "1")
    @NotNull(message = "dish id cannot be null")
    private Long dishId;

    @Schema(description = "Dish quantity", example = "2")
    @NotNull(message = "quantity cannot be null")
    @Min(value = 1, message = "quantity must be greater than or equal to 1")
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
