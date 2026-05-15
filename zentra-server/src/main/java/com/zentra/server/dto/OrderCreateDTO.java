package com.zentra.server.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * DTO for order creation
 */
@Schema(description = "Order creation request")
public class OrderCreateDTO {

    @ArraySchema(
            schema = @Schema(
                    implementation = OrderItemCreateDTO.class
            )
    )
    @Schema(description = "Order item list")
    @Valid
    @NotEmpty(message = "order items cannot be empty")
    private List<OrderItemCreateDTO> items;

    // Getter and Setter
    public List<OrderItemCreateDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemCreateDTO> items) {
        this.items = items;
    }
}
