package org.insertcoin.productservice.controllers;

import org.insertcoin.productservice.dtos.AssignProductKeysRequestDTO;
import org.insertcoin.productservice.dtos.AssignedKeysResponseDTO;
import org.insertcoin.productservice.dtos.ProductKeyImportResponseDTO;
import org.insertcoin.productservice.services.ProductKeyAssignService;
import org.insertcoin.productservice.services.ProductKeyImportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/internal/product-keys")
public class ProductKeyInternalController {

    private final ProductKeyImportService importService;
    private final ProductKeyAssignService assignService;

    public ProductKeyInternalController(ProductKeyImportService importService, ProductKeyAssignService assignService) {
        this.importService = importService;
        this.assignService = assignService;
    }

    @PostMapping("/import")
    public ResponseEntity<ProductKeyImportResponseDTO> importCsv(@RequestParam("file") MultipartFile file) {
        try {
            ProductKeyImportResponseDTO result = importService.importCsv(file);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            throw new RuntimeException("Error importing CSV: " + e.getMessage());
        }
    }

    @PostMapping("/assign")
    public ResponseEntity<AssignedKeysResponseDTO> assign(@RequestBody AssignProductKeysRequestDTO request) {
        return ResponseEntity.ok(assignService.assignKeys(request));
    }
}