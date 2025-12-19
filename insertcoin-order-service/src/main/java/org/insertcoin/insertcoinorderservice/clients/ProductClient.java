package org.insertcoin.insertcoinorderservice.clients;

import org.insertcoin.insertcoinorderservice.dtos.request.AssignProductKeysRequestDTO;
import org.insertcoin.insertcoinorderservice.dtos.response.AssignedKeysResponseDTO;
import org.insertcoin.insertcoinorderservice.dtos.response.ProductResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(
        name = "insertcoin-product-service"
)
public interface ProductClient {

    @GetMapping("/products/{id}")
    ProductResponseDTO findById(
            @PathVariable UUID id,
            @RequestHeader("Authorization") String token
    );

    @PostMapping("/internal/product-keys/assign")
    AssignedKeysResponseDTO assignKeys(@RequestBody AssignProductKeysRequestDTO request);
}
