package org.insertcoin.productservice.repositories;

import org.insertcoin.productservice.entities.ProductRatingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductRatingRepository extends JpaRepository<ProductRatingEntity, UUID> {
    List<ProductRatingEntity> findByProductId(UUID productId);
}