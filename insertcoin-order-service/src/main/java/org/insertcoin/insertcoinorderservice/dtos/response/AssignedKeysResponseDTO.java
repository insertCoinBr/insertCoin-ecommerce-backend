package org.insertcoin.insertcoinorderservice.dtos.response;

import java.util.List;
import java.util.UUID;

public class AssignedKeysResponseDTO {
    private UUID orderId;
    private List<AssignedKeysPerProductDTO> items;

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public List<AssignedKeysPerProductDTO> getItems() {
        return items;
    }

    public void setItems(List<AssignedKeysPerProductDTO> items) {
        this.items = items;
    }
}

