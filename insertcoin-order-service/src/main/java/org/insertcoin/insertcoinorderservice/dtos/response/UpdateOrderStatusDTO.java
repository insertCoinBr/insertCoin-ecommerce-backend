package org.insertcoin.insertcoinorderservice.dtos.response;

public class UpdateOrderStatusDTO {

    private String status;

    public UpdateOrderStatusDTO() {}

    public UpdateOrderStatusDTO(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}