package com.zentra.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Category pagination API response
 */
@Schema(description = "Category pagination API response")
public class CategoryPageResponseDTO extends BaseResponseDTO{

    @Schema(description = "Category pagination data")
    private PageDataDTO<CategoryDTO> data;

    // Getter and Setter
    public PageDataDTO<CategoryDTO> getData() {
        return data;
    }

    public void setData(PageDataDTO<CategoryDTO> data) {
        this.data = data;
    }
}
