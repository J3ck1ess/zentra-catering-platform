package com.zentra.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Generate API response with data
 *
 * @param <T> response data type
 */
@Schema(description = "API response with data")
public class DataResponseDTO<T> extends BaseResponseDTO {

    @Schema(description = "Response data")
    private T data;

    // Getter and setter
    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
