package com.zentra.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Dish pagination API response
 */
@Schema(description = "Dish pagination API response")
public class DishPageResponseDTO extends BaseResponseDTO{

    @Schema(description = "Dish pagination data")
    private PageDataDTO<DishDTO> data;

    // Getter and Setter
    public PageDataDTO<DishDTO> getData() {
        return data;
    }

    public void setData(PageDataDTO<DishDTO> data) {
        this.data = data;
    }
}
