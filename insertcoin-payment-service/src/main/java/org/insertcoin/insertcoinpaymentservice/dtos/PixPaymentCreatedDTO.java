package org.insertcoin.insertcoinpaymentservice.dtos;

import java.math.BigDecimal;
import java.util.UUID;

public class PixPaymentCreatedDTO {
    private UUID orderId;
    private String orderNumber;
    private String customerEmail;
    private String qrCode;
    private BigDecimal amount;

    public PixPaymentCreatedDTO(UUID orderId, String orderNumber, String customerEmail, String qrCode, BigDecimal amount) {
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.customerEmail = customerEmail;
        this.qrCode = qrCode;
        this.amount = amount;
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

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
