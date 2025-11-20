package org.insertcoin.insertcoinorderservice.dtos.response;

import java.time.LocalDateTime;
import java.util.UUID;

public class OrderAdminSearchResponseDTO {

    private UUID orderId;
    private String orderNumber;
    private String status;
    private LocalDateTime createdAt;

    public OrderAdminSearchResponseDTO(UUID orderId, String orderNumber, String status, LocalDateTime createdAt) {
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.status = status;
        this.createdAt = createdAt;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
