package org.insertcoin.insertcoinorderservice.clients;

import org.insertcoin.insertcoinorderservice.dtos.response.ProductResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@FeignClient(
        name = "insertcoin-product-service",
        path = "/products"
)
public interface ProductClient {

    @GetMapping("/{id}")
    ProductResponseDTO findById(
            @PathVariable UUID id,
            @RequestHeader("Authorization") String token
    );
}
