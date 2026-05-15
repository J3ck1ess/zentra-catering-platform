package com.zentra.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Order pagination API response
 */
@Schema(description = "Order pagination API response")
public class OrderPageResponseDTO extends BaseResponseDTO {

    @Schema(description = "Order pagination data")
    private PageDataDTO<OrderPageDTO> data;

    // Getter and Setter
    public PageDataDTO<OrderPageDTO> getData() {
        return data;
    }

    public void setData(PageDataDTO<OrderPageDTO> data) {
        this.data = data;
    }
}
