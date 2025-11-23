package org.insertcoin.productservice.services;

import jakarta.transaction.Transactional;
import org.insertcoin.productservice.dtos.*;
import org.insertcoin.productservice.entities.ProductEntity;
import org.insertcoin.productservice.entities.ProductKeyEntity;
import org.insertcoin.productservice.repositories.ProductKeyRepository;
import org.insertcoin.productservice.repositories.ProductRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductKeyAssignService {

    private final ProductKeyRepository repository;
    private final ProductRepository productRepository;

    public ProductKeyAssignService(ProductKeyRepository repository, ProductRepository productRepository) {
        this.repository = repository;
        this.productRepository = productRepository;
    }

    private String findProductName(UUID productId) {
        return productRepository.findById(productId)
                .map(ProductEntity::getName)
                .orElse("Unknown product");
    }

    @Transactional
    public AssignedKeysResponseDTO assignKeys(AssignProductKeysRequestDTO request) {

        List<AssignedKeysPerProductDTO> result = new ArrayList<>();

        for (AssignProductKeysItemDTO item : request.getItems()) {

            Pageable pageable = PageRequest.of(0, item.getQuantity());

            List<ProductKeyEntity> keys = repository.findByProductIdAndStatus(
                    item.getProductId(),
                    "AVAILABLE",
                    pageable
            );

            if (keys.size() < item.getQuantity()) {
                throw new RuntimeException("Not enough keys for product ID: " + item.getProductId());
            }

            // atualizar as chaves como vendidas
            keys.forEach(k -> {
                k.setStatus("SOLD");
                k.setOrderId(request.getOrderId());
                k.setSoldAt(LocalDateTime.now());
            });

            repository.saveAll(keys);

            result.add(
                    new AssignedKeysPerProductDTO(
                            item.getProductId(),
                            findProductName(item.getProductId()),
                            keys.stream().map(ProductKeyEntity::getKeyCode).collect(Collectors.toList())
                    )
            );
        }

        return new AssignedKeysResponseDTO(request.getOrderId(), result);
    }
}