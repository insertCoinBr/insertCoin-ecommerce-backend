package org.insertcoin.productservice.repositories;

import org.insertcoin.productservice.entities.ProductKeyEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductKeyRepository extends JpaRepository<ProductKeyEntity, UUID> {
    Optional<ProductKeyEntity> findByKeyCode(String keyCode);

    List<ProductKeyEntity> findByProductIdAndStatus(
            UUID productId,
            String status,
            Pageable pageable
    );

}
