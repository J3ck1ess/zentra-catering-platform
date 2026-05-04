package com.zentra.server.dto;

import java.util.List;

public class OrderCreateDTO {

    private List<OrderItemCreateDTO> items;

    // Getter and Setter
    public List<OrderItemCreateDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemCreateDTO> items) {
        this.items = items;
    }
}
