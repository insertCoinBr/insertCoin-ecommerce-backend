package org.insertcoin.insertcoinorderservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class InsertcoinOrderServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(InsertcoinOrderServiceApplication.class, args);
    }
}