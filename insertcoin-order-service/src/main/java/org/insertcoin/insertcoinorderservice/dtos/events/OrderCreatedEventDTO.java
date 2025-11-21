package org.insertcoin.insertcoinorderservice.dtos.events;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class OrderCreatedEventDTO {

    private UUID orderId;
    private String OrderNumber;
    private BigDecimal amount;
    private String customerEmail;
    private String paymentMethod;
    private CardDTO card;
    private List<ProductDTO> products;
    private String currency;

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public String getOrderNumber() {
        return OrderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        OrderNumber = orderNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public static class CardDTO {
        private String number;
        private String holderName;
        private int expiryMonth;
        private int expiryYear;
        private String cvv;

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public String getHolderName() {
            return holderName;
        }

        public void setHolderName(String holderName) {
            this.holderName = holderName;
        }

        public int getExpiryMonth() {
            return expiryMonth;
        }

        public void setExpiryMonth(int expiryMonth) {
            this.expiryMonth = expiryMonth;
        }

        public int getExpiryYear() {
            return expiryYear;
        }

        public void setExpiryYear(int expiryYear) {
            this.expiryYear = expiryYear;
        }

        public String getCvv() {
            return cvv;
        }

        public void setCvv(String cvv) {
            this.cvv = cvv;
        }
    }

    public CardDTO getCard() {
        return card;
    }

    public void setCard(CardDTO card) {
        this.card = card;
    }

    public static class ProductDTO {
        private String productName;
        private int quantity;

        public ProductDTO(String productName, int quantity) {
            this.productName = productName;
            this.quantity = quantity;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
    }

    public List<ProductDTO> getProducts() {
        return products;
    }

    public void setProducts(List<ProductDTO> products) {
        this.products = products;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
