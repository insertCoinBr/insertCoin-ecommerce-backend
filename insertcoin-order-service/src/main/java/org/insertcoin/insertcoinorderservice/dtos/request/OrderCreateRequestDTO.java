package org.insertcoin.insertcoinorderservice.dtos.request;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public class OrderCreateRequestDTO {

    @NotNull
    private List<OrderItemRequestDTO> items;

    @NotNull
    private String paymentMethod;

    public List<OrderItemRequestDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemRequestDTO> items) {
        this.items = items;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
