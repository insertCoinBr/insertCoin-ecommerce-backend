package org.insertcoin.insertcoinorderservice.dtos.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public class CreateOrderRequestDTO {

    @NotNull
    @Size(min = 1)
    private List<OrderItemRequestDTO> items;

    @NotNull
    private String paymentMethod; // PIX | CARD

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
