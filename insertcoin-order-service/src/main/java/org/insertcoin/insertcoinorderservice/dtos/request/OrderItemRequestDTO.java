package org.insertcoin.insertcoinorderservice.dtos.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class OrderItemRequestDTO {

    @NotNull
    private UUID productId;

    @NotNull
    private Integer quantity;

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
