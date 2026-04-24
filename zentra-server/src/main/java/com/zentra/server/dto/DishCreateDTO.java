package com.zentra.server.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;

/**
 * DTO for creating dish
 */
public class DishCreateDTO {

    @NotNull(message = "name cannot be null")
    private String name;

    @NotNull(message = "price cannot be null")
    @DecimalMin(value = "0.0", message = "price must be greater than 0")
    private BigDecimal price;

    private Long categoryId;

    private Integer status;

    // getters & setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
