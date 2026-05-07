package com.zentra.server.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class OrderCreateDTO {

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
