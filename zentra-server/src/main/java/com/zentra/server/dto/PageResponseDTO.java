package com.zentra.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Generic paginated API response
 *
 * @param <T> record type
 */
@Schema(description = "Paginated API response")
public class PageResponseDTO<T> extends BaseResponseDTO{

    @Schema(description = "Pagination response data")
    private PageDataDTO<T> data;

    // Getter and Setter
    public PageDataDTO<T> getData() {
        return data;
    }

    public void setData(PageDataDTO<T> data) {
        this.data = data;
    }
}
