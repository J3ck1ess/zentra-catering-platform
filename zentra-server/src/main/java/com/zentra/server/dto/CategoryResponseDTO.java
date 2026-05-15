package com.zentra.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Category API response
 */
@Schema(description = "Category API response")
public class CategoryResponseDTO extends BaseResponseDTO{

    @Schema(description = "Category response data")
    private CategoryDTO data;

    // Getter and Setter
    public CategoryDTO getData() {
        return data;
    }

    public void setData(CategoryDTO data) {
        this.data = data;
    }
}
