package org.insertcoin.insertcoinpaymentservice.dtos;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class PixPaymentConfirmedDTO {
    private UUID orderId;
    private String orderNumber;
    private String customerEmail;
    private BigDecimal amount;
    private String currency;
    private List<OrderProductDTO> products;

    public PixPaymentConfirmedDTO(UUID orderId, String orderNumber, String customerEmail, BigDecimal amount, String currency, List<OrderProductDTO> products) {
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.customerEmail = customerEmail;
        this.amount = amount;
        this.currency = currency;
        this.products = products;
    }

    public PixPaymentConfirmedDTO() {

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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public List<OrderProductDTO> getProducts() {
        return products;
    }

    public void setProducts(List<OrderProductDTO> products) {
        this.products = products;
    }
}
