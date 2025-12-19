package org.insertcoin.productservice.controllers;

import org.insertcoin.productservice.dtos.AddProductRequestDTO;
import org.insertcoin.productservice.dtos.EditProductRequestDTO;
import org.insertcoin.productservice.dtos.ProductKeyImportResponseDTO;
import org.insertcoin.productservice.services.ProductKeyImportService;
import org.insertcoin.productservice.services.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/products/admin")
public class ProductAdminController {

    private final ProductService productService;
    private final ProductKeyImportService importService;

    public ProductAdminController(ProductService productService, ProductKeyImportService importService) {
        this.productService = productService;
        this.importService = importService;
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

    @PreAuthorize("hasAuthority('PRODUCTS_ADMIN')")
    @PostMapping("/product-keys/import")
    public ResponseEntity<ProductKeyImportResponseDTO> importCsv(@RequestParam("file") MultipartFile file) {
        try {
            ProductKeyImportResponseDTO result = importService.importCsv(file);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            throw new RuntimeException("Error importing CSV: " + e.getMessage());
        }
    }
}

