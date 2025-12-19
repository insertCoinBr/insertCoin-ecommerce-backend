package org.insertcoin.productservice.controllers;

import org.insertcoin.productservice.dtos.AssignProductKeysRequestDTO;
import org.insertcoin.productservice.dtos.AssignedKeysResponseDTO;
import org.insertcoin.productservice.services.ProductKeyAssignService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/product-keys")
public class ProductKeyInternalController {

    private final ProductKeyAssignService assignService;

    public ProductKeyInternalController(ProductKeyAssignService assignService) {
        this.assignService = assignService;
    }

    @PostMapping("/assign")
    public ResponseEntity<AssignedKeysResponseDTO> assign(@RequestBody AssignProductKeysRequestDTO request) {
        return ResponseEntity.ok(assignService.assignKeys(request));
    }
}