package org.insertcoin.productservice.repositories;


import jakarta.transaction.Transactional;
import org.insertcoin.productservice.entities.PlatformEntity;
import org.insertcoin.productservice.entities.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, UUID> {
    Optional<ProductEntity> findById(UUID id_product);

    @Modifying
    @Transactional
    @Query(
            value = "INSERT INTO product_categories (id_product, id_category) VALUES (:productId, :categoryId)",
            nativeQuery = true
    )
    void insertCategory(UUID productId, int categoryId);

    @Modifying
    @Transactional
    @Query(
            value = "DELETE FROM product_categories WHERE id_product = :productId",
            nativeQuery = true
    )
    void deleteCategories(UUID productId);

}
