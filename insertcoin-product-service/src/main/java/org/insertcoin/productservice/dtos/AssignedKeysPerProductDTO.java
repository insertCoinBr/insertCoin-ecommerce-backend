package org.insertcoin.productservice.dtos;

import java.util.List;
import java.util.UUID;

public class AssignedKeysPerProductDTO {

    private UUID productId;
    private String productName;
    private List<String> keys;

    public AssignedKeysPerProductDTO(UUID productId, String productName, List<String> keys) {
        this.productId = productId;
        this.productName = productName;
        this.keys = keys;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public List<String> getKeys() {
        return keys;
    }

    public void setKeys(List<String> keys) {
        this.keys = keys;
    }
}
