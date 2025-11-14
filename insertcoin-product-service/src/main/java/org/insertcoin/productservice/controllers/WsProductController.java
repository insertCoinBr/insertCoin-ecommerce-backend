package org.insertcoin.productservice.controllers;

import org.insertcoin.productservice.dtos.AddProductRequestDTO;
import org.insertcoin.productservice.dtos.EditProductRequestDTO;
import org.insertcoin.productservice.dtos.RemoveProductRequestDTO;
import org.insertcoin.productservice.services.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/ws/products")
public class WsProductController {

    private final ProductService productService;

    public WsProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<?> addProduct(@RequestBody AddProductRequestDTO dto) {
        var result = productService.createProduct(dto);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeProduct(
            @PathVariable UUID id,
            @RequestBody RemoveProductRequestDTO dto
    ) {
        productService.deleteProduct(id, dto.gameId());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editProduct(
            @PathVariable UUID id,
            @RequestBody EditProductRequestDTO dto
    ) {
        var result = productService.updateProduct(id, dto);
        return ResponseEntity.ok(result);
    }
}

