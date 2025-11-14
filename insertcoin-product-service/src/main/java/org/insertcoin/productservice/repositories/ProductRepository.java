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
    Optional<ProductEntity> findById(UUID id);
    Optional<ProductEntity> findByName(String name);
    Optional<ProductEntity> findByGameId(String gameId);
    Optional<ProductEntity> findByIdAndGameId(UUID id, String gameId);
    long countByPlatform(PlatformEntity platform);

    @Modifying
    @Transactional
    @Query(
            value = "INSERT INTO product_categories (product_id, category_id) VALUES (:productId, :categoryId)",
            nativeQuery = true
    )
    void insertCategory(UUID productId, int categoryId);


    @Modifying
    @Transactional
    @Query(
            value = "DELETE FROM product_categories WHERE product_id = :productId",
            nativeQuery = true
    )
    void deleteCategories(UUID productId);

}
