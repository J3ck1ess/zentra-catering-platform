package com.zentra.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * DTO for updating dish
 */
@Schema(description = "Dish update request")
public class DishUpdateDTO {

    @Schema(description = "Dish ID", example = "1")
    @NotNull(message = "dish id cannot be null")
    private Long id;

    @Schema(description = "Dish name", example = "Pizza")
    @Size(min = 1, message = "name cannot be empty")
    private String name;

    @Schema(description = "Dish price", example = "10.99")
    @DecimalMin(value = "0.01", message = "price must be greater than 0.01")
    private BigDecimal price;

    @Schema(description = "Category ID", example = "1")
    private Long categoryId;

    @Schema(description = "Dish status (1 = Enabled, 0 = Disabled)", example = "1")
    private Integer status;

    // Getter and Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
