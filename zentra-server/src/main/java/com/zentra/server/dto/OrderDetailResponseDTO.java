package com.zentra.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Order detail API response
 */
@Schema(description = "Order detail API response")
public class OrderDetailResponseDTO extends BaseResponseDTO{

    @Schema(description = "Order detail response data")
    private OrderDetailDTO data;

    // Getter and Setter
    public OrderDetailDTO getData() {
        return data;
    }

    public void setData(OrderDetailDTO data) {
        this.data = data;
    }
}
