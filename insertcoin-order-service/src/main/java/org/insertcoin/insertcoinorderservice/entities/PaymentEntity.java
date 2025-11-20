package org.insertcoin.insertcoinorderservice.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payment")
public class PaymentEntity {

    @Id
    @GeneratedValue
    @Column(name = "id_payment", columnDefinition = "UUID")
    private UUID id;

    @Column(name = "id_order", columnDefinition = "UUID", nullable = false)
    private UUID orderId;

    @Column(name = "payment_method", nullable = false)
    private String paymentMethod;

    private BigDecimal amount;

    private String status;

    private String transactionId;
    private String pixKey;
    private String pixPayload;

    private LocalDateTime paidAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getPixKey() {
        return pixKey;
    }

    public void setPixKey(String pixKey) {
        this.pixKey = pixKey;
    }

    public String getPixPayload() {
        return pixPayload;
    }

    public void setPixPayload(String pixPayload) {
        this.pixPayload = pixPayload;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
