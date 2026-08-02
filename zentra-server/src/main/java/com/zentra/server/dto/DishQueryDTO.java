package com.zentra.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;

/**
 * Query object for dish list
 */
@Schema(description = "Dish query request")
public class DishQueryDTO extends BasePageQueryDTO {

    /**
     * Optional dish name filter
     */
    @Schema(description = "Dish name filter", example = "Pizza")
    private String name;

    /**
     * Optional category filter
     */
    @Schema(description = "Category ID filter", example = "1")
    private Long categoryId;

    /**
     * Optional status filter
     */
    @Schema(description = "Dish status filter (1 = Enabled, 0 = Disabled)", example = "1")
    private Integer status;

    // Getter and Setter
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
