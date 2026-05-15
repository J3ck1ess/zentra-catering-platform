package com.zentra.server.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.cglib.core.Local;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for order detail response
 */
@Schema(description = "Order detail response")
public class OrderDetailDTO {

    @Schema(description = "Order ID", example = "1")
    private Long id;

    @Schema(description = "Order number", example = "1746628823000")
    private String orderNumber;

    @Schema(description = "Total order amount", example = "35.98")
    private BigDecimal totalAmount;

    @Schema(description = "Order status (1 = Pending, 2 = Confirmed, 3 = Completed, 4 = Cancelled)", example = "1")
    private Integer status;

    @Schema(description = "Order creation time", example = "2026-05-08T14:22:30")
    private LocalDateTime createdAt;

    @ArraySchema(
            schema = @Schema(
                    implementation = OrderItemDTO.class
            )
    )
    @Schema(description = "Order item list")
    private List<OrderItemDTO> items;

    // Getter and Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<OrderItemDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemDTO> items) {
        this.items = items;
    }
}
