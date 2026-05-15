package com.zentra.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Dish API response
 */
@Schema(description = "Dish API response")
public class DishResponseDTO extends BaseResponseDTO{

    @Schema(description = "Dish response data")
    private DishDTO data;

    // Getter and Setter
    public DishDTO getData() {
        return data;
    }

    public void setData(DishDTO data) {
        this.data = data;
    }
}
