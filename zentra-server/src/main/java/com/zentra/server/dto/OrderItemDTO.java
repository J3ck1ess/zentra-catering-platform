package com.zentra.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

/**
 * DTO for order item response
 */
@Schema(description = "Order item response")
public class OrderItemDTO {

    @Schema(description = "Dish ID", example = "1")
    private Long dishId;

    @Schema(description = "Dish name", example = "Pizza")
    private String dishName;

    @Schema(description = "Dish price", example = "10.99")
    private BigDecimal price;

    @Schema(description = "Dish quantity", example = "2")
    private Integer quantity;

    @Schema(description = "Subtotal amount", example = "25.98")
    private BigDecimal amount;

    // Getter and Setter
    public Long getDishId() {
        return dishId;
    }

    public void setDishId(Long dishId) {
        this.dishId = dishId;
    }

    public String getDishName() {
        return dishName;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
