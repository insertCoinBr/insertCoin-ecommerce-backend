package org.insertcoin.productservice.controllers;

import org.insertcoin.productservice.dtos.ProductResponseDTO;
import org.insertcoin.productservice.mappers.ProductMapper;
import org.insertcoin.productservice.services.ProductService;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getOne(
            @PathVariable UUID id,
            @RequestParam(value = "curr", required = false) String targetCurrency
    ) {
        var product = productService.findOneWithCurrency(id, targetCurrency);
        return ResponseEntity.ok(product);
    }

    // -------------------------------
    // GET /products?curr=BRL
    // -------------------------------
    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getProducts(
            @RequestParam(defaultValue = "BRL") String curr) {

        var entities = productService.findAllWithCurrency(curr);
        var response = entities.stream()
                .map(productMapper::toResponseDTO)
                .toList();
        return ResponseEntity.ok(response);
    }
   // -------------------------------
   // POST /products/rating/{gameId}
   // -------------------------------
   @PostMapping("/rating/{gameId}")
   public ResponseEntity<ProductResponseDTO> addRating(
           @PathVariable String gameId,
           @RequestBody RatingDTO body
   ) {
       var product = productService.addRating(gameId, body.rating());
       return ResponseEntity.ok(productMapper.toResponseDTO(product));
   }

    public record RatingDTO(Double rating) {}

}
