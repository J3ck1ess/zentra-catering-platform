package com.zentra.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

/**
 * DTO for dish response
 */
@Schema(description = "Dish response")
public class DishDTO {

    @Schema(description = "Dish ID", example = "1")
    private Long id;

    @Schema(description = "Dish name", example = "Pizza")
    private String name;

    @Schema(description = "Dish price", example = "10.99")
    private BigDecimal price;

    @Schema(description = "Dish status (1 = Enabled, 0 = Disabled)", example = "1")
    private Integer status;

    @Schema(description = "Category ID", example = "1")
    private Long categoryId;

    @Schema(description = "Category name", example = "Pizza")
    private String categoryName;

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

    public Integer getStatus() {

        return status;
    }

    public void setStatus(Integer status) {

        this.status = status;
    }

    public Long getCategoryId() {

        return categoryId;
    }

    public void setCategoryId(Long categoryId) {

        this.categoryId = categoryId;
    }

    public String getCategoryName() {

        return categoryName;
    }

    public void setCategoryName(String categoryName) {

        this.categoryName = categoryName;
    }
}
