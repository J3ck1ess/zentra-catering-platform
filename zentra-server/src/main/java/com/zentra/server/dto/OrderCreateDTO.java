package com.zentra.server.dto;

import java.util.List;

public class OrderCreateDTO {

    private List<OrderItemDTO> items;

    // Getter and Setter
    public List<OrderItemDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemDTO> items) {
        this.items = items;
    }
}
