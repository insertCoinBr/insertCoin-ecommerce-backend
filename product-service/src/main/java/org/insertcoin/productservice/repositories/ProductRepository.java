package org.insertcoin.productservice.repositories;


import org.insertcoin.productservice.entities.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, UUID> {
    Optional<ProductEntity> findById(UUID id);
    Optional<ProductEntity> findByName(String name);
    Optional<ProductEntity> findByGameId(String gameId);
    void deleteByGameId(String gameId);
}
