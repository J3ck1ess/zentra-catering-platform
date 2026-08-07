package com.zentra.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO for dashboard statistics response
 */
@Schema(description = "Dashboard statistics response")
public class DashboardDTO {

    @Schema(description = "Employee count", example = "12")
    private Long employeeCount;

    @Schema(description = "User count", example = "138")
    private Long userCount;

    @Schema(description = "Category count", example = "18")
    private Long categoryCount;

    @Schema(description = "Dish count", example = "53")
    private Long dishCount;

    @Schema(description = "Pending order count", example = "6")
    private Long pendingOrderCount;

    @Schema(description = "Paid order count", example = "15")
    private Long paidOrderCount;

    @Schema(description = "Completed order count", example = "82")
    private Long completedOrderCount;

    @Schema(description = "Cancelled order count", example = "4")
    private Long cancelledOrderCount;

    public Long getEmployeeCount() {
        return employeeCount;
    }

    public void setEmployeeCount(Long employeeCount) {
        this.employeeCount = employeeCount;
    }

    public Long getUserCount() {
        return userCount;
    }

    public void setUserCount(Long userCount) {
        this.userCount = userCount;
    }

    public Long getCategoryCount() {
        return categoryCount;
    }

    public void setCategoryCount(Long categoryCount) {
        this.categoryCount = categoryCount;
    }

    public Long getDishCount() {
        return dishCount;
    }

    public void setDishCount(Long dishCount) {
        this.dishCount = dishCount;
    }

    public Long getPendingOrderCount() {
        return pendingOrderCount;
    }

    public void setPendingOrderCount(Long pendingOrderCount) {
        this.pendingOrderCount = pendingOrderCount;
    }

    public Long getPaidOrderCount() {
        return paidOrderCount;
    }

    public void setPaidOrderCount(Long paidOrderCount) {
        this.paidOrderCount = paidOrderCount;
    }

    public Long getCompletedOrderCount() {
        return completedOrderCount;
    }

    public void setCompletedOrderCount(Long completedOrderCount) {
        this.completedOrderCount = completedOrderCount;
    }

    public Long getCancelledOrderCount() {
        return cancelledOrderCount;
    }

    public void setCancelledOrderCount(Long cancelledOrderCount) {
        this.cancelledOrderCount = cancelledOrderCount;
    }

}