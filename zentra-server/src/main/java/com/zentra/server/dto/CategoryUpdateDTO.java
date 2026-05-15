package com.zentra.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for updating category
 */
@Schema(description = "Category update request")
public class CategoryUpdateDTO {

    @Schema(description = "Category ID", example = "1")
    @NotNull(message = "category id cannot be null")
    private Long id;

    @Schema(description = "Category name", example = "Food")
    private String name;

    @Schema(description = "Category type (1 = Dish, 2 = Set Meal)", example = "1")
    private Integer type;

    @Schema(description = "Category status (1 = Enabled, 2 = Disabled)", example = "1")
    private Integer status;

    @Schema(description = "Category sort order", example = "10")
    private Integer sort;

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
}
