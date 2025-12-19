package org.insertcoin.insertcoinpaymentservice.dtos;

public class CustomerEmailDTO {

    private String customerEmail;

    public CustomerEmailDTO() {
    }

    public CustomerEmailDTO(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }
}
