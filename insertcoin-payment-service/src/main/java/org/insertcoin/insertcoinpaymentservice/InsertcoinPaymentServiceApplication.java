package org.insertcoin.insertcoinpaymentservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class InsertcoinPaymentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InsertcoinPaymentServiceApplication.class, args);
    }

}
