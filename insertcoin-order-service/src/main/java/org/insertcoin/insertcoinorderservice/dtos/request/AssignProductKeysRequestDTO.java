package org.insertcoin.insertcoinorderservice.dtos.request;

import java.util.List;
import java.util.UUID;

public class AssignProductKeysRequestDTO {
    private UUID orderId;
    private List<AssignProductKeysItemDTO> items;

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public List<AssignProductKeysItemDTO> getItems() {
        return items;
    }

    public void setItems(List<AssignProductKeysItemDTO> items) {
        this.items = items;
    }
}

