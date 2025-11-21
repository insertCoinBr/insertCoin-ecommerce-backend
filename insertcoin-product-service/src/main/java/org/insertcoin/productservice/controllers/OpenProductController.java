package org.insertcoin.productservice.controllers;

import org.insertcoin.productservice.dtos.ProductResponseDTO;
import org.insertcoin.productservice.entities.CategoryEntity;
import org.insertcoin.productservice.mappers.ProductMapper;
import org.insertcoin.productservice.services.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/products")
public class OpenProductController {

    private final ProductService productService;
    private final ProductMapper productMapper;

    public OpenProductController(ProductService productService, ProductMapper productMapper) {
        this.productService = productService;
        this.productMapper = productMapper;
    }

    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getOne(
            @PathVariable UUID id,
            @RequestParam(value = "curr", required = false) String targetCurrency
    ) {
        var product = productService.findOneWithCurrency(id, targetCurrency);
        return ResponseEntity.ok(product);
    }

    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getProducts(
            @RequestParam(defaultValue = "BRL") String curr) {

        var entities = productService.findAllWithCurrency(curr);
        var response = entities.stream()
                .map(productMapper::toResponseDTO)
                .toList();
        return ResponseEntity.ok(response);
    }

   @PreAuthorize("hasRole('CLIENT')")
   @PostMapping("/rating/{id}")
   public ResponseEntity<ProductResponseDTO> addRating(
           @PathVariable UUID id,
           @RequestBody RatingDTO body
   ) {
       var product = productService.addRating(id, body.rating());
       return ResponseEntity.ok(productMapper.toResponseDTO(product));
   }

    public record RatingDTO(Double rating) {}


    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("/categories")
    public List<String> getCategoryNames() {
        return productService.findAllCategories()
                .stream()
                .map(CategoryEntity::getName)
                .toList();
    }


    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("/platforms")
    public ResponseEntity<?> getPlatforms() {
        return ResponseEntity.ok(productService.findAllPlatforms());
    }
}
