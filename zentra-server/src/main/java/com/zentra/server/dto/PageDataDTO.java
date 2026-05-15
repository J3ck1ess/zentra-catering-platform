package com.zentra.server.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * Generic pagination data structure
 *
 * @param <T> record type
 */
@Schema(description = "Pagination data")
public class PageDataDTO<T> {

    @Schema(description = "Total number of records", example = "100")
    private Long total;

    @ArraySchema(schema = @Schema(description = "List of records"))
    private List<T> records;

    // Getter and Setter
    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<T> getRecords() {
        return records;
    }

    public void setRecords(List<T> records) {
        this.records = records;
    }
}
