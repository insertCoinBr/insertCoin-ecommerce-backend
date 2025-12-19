package org.insertcoin.productservice.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "product_key")
public class ProductKeyEntity {

    @Id
    @Column(name = "id_key")
    private UUID idKey = UUID.randomUUID();

    @Column(name = "id_product", nullable = false)
    private UUID productId;

    @Column(name = "key_code", nullable = false, unique = true)
    private String keyCode;

    @Column(name = "status")
    private String status = "AVAILABLE";

    @Column(name = "id_order")
    private UUID orderId;

    @Column(name = "sold_at")
    private LocalDateTime soldAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public UUID getIdKey() {
        return idKey;
    }

    public void setIdKey(UUID idKey) {
        this.idKey = idKey;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public String getKeyCode() {
        return keyCode;
    }

    public void setKeyCode(String keyCode) {
        this.keyCode = keyCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public LocalDateTime getSoldAt() {
        return soldAt;
    }

    public void setSoldAt(LocalDateTime soldAt) {
        this.soldAt = soldAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

