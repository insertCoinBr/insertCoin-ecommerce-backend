package org.insertcoin.productservice.repositories;

import org.insertcoin.productservice.entities.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Integer> {
    Optional<CategoryEntity> findByName(String name);
        @Query(value = """
        SELECT c.* 
        FROM category c
        JOIN product_categories pc 
            ON pc.id_category = c.id
        WHERE pc.id_product = :productId
    """, nativeQuery = true)
        List<CategoryEntity> findByProductId(UUID productId);



}
