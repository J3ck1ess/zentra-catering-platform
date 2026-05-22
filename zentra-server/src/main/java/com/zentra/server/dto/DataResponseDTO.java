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

    /**
     * Success response
     */
    public static <T> DataResponseDTO<T> success(
            T data
    ) {

        DataResponseDTO<T> response = new DataResponseDTO<>();

        response.setCode(0);
        response.setMsg("success");
        response.setData(data);

        return response;
    }

    // Getter and setter
    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
