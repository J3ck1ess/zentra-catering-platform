package com.zentra.server.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Query object for category
 */
@Schema(description = "Category query request")
public class CategoryQueryDTO extends BasePageQueryDTO{

    /**
     * Optional type filter
     */
    @Schema(description = "Category type filter (1 = Dish, 2 = Set Meal)", example = "1")
    private Integer type;

    /**
     * Optional status filter
     */
    @Schema(description = "Category status filter (1 = Enabled, 2 = Disabled)", example = "1")
    private Integer status;

    // Getter and Setter
    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
