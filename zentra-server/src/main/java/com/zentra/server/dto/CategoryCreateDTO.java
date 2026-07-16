package com.zentra.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for category creation
 */
@Schema(description = "Category creation request")
public class CategoryCreateDTO {

    @Schema(description = "Category name", example = "Food")
    @NotBlank(message = "name cannot be blank")
    private String name;

    @Schema(description = "Category type (1 = Dish, 2 = Set Meal", example = "1")
    @NotNull(message = "type cannot be null")
    private Integer type;

    @Schema(description = "Category status (1 = Enabled, 0 = Disabled)", example = "1")
    private Integer status;

    @Schema(description = "Category sort order", example = "10")
    private Integer sort;

    @Schema(description = "Category description", example = "Hot dishes")
    private String description;

    // Getter and Setter
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
