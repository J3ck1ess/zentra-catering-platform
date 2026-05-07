package com.zentra.server.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;

/**
 * DTO for creating dish
 */
public class DishCreateDTO {

    @NotBlank(message = "name cannot be blank")
    private String name;

    @NotNull(message = "price cannot be null")
    @DecimalMin(value = "0.01", message = "price must be greater than 0.01")
    private BigDecimal price;

    @NotNull(message = "categoryId cannot be null")
    private Long categoryId;

    private Integer status;

    // Getter and Setter

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
