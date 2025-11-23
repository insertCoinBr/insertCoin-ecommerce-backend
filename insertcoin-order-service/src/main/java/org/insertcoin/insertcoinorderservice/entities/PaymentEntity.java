package org.insertcoin.insertcoinorderservice.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payment")
public class PaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_payment")
    private UUID id;

    @Column(name = "id_order", nullable = false)
    private UUID orderId;

    @Column(name = "payment_method", columnDefinition = "TEXT", nullable = false)
    private String paymentMethod;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(columnDefinition = "TEXT")
    private String status;

    @Column(name = "transaction_id", columnDefinition = "TEXT")
    private String transactionId;

    @Column(name = "pix_payload", columnDefinition = "TEXT")
    private String pixPayload;

    @Column(name = "pix_key", columnDefinition = "TEXT")
    private String pixKey;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public PaymentEntity(
            UUID orderId,
            String paymentMethod,
            BigDecimal amount,
            String status,
            String transactionId,
            String pixPayload,
            String pixKey,
            LocalDateTime paidAt,
            LocalDateTime createdAt
    ) {
        this.orderId = orderId;
        this.paymentMethod = paymentMethod;
        this.amount = amount;
        this.status = status;
        this.transactionId = transactionId;
        this.pixPayload = pixPayload;
        this.pixKey = pixKey;
        this.paidAt = paidAt;
        this.createdAt = createdAt;
    }

    public PaymentEntity() {

    }

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

    public String getPixPayload() {
        return pixPayload;
    }

    public void setPixPayload(String pixPayload) {
        this.pixPayload = pixPayload;
    }

    public String getPixKey() {
        return pixKey;
    }

    public void setPixKey(String pixKey) {
        this.pixKey = pixKey;
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
