package org.insertcoin.productservice.dtos;

import java.util.List;
import java.util.UUID;

public class AssignedKeysResponseDTO {

    private UUID orderId;
    private List<AssignedKeysPerProductDTO> items;

    public AssignedKeysResponseDTO(UUID orderId, List<AssignedKeysPerProductDTO> items) {
        this.orderId = orderId;
        this.items = items;
    }

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
