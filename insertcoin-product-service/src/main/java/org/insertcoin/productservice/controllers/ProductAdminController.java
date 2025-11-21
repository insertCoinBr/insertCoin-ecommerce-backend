package org.insertcoin.productservice.controllers;

import org.insertcoin.productservice.dtos.AddProductRequestDTO;
import org.insertcoin.productservice.dtos.EditProductRequestDTO;
import org.insertcoin.productservice.services.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/products/admin")
public class ProductAdminController {

    private final ProductService productService;

    public ProductAdminController(ProductService productService) {
        this.productService = productService;
    }

    @PreAuthorize("hasAuthority('PRODUCTS_ADMIN')")
    @PostMapping("/addProduct")
    public ResponseEntity<?> addProduct(@RequestBody AddProductRequestDTO dto) {
        var result = productService.createProduct(dto);
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasAuthority('PRODUCTS_ADMIN')")
    @DeleteMapping("/removeProduct/{id}")
    public ResponseEntity<?> removeProduct(
            @PathVariable UUID id

    ) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('PRODUCTS_ADMIN')")
    @PutMapping("/updateProduct/{id}")
    public ResponseEntity<?> editProduct(
            @PathVariable UUID id,
            @RequestBody EditProductRequestDTO dto
    ) {
        var result = productService.updateProduct(id, dto);
        return ResponseEntity.ok(result);
    }
}

