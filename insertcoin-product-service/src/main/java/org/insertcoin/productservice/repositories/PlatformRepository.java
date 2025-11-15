package org.insertcoin.productservice.repositories;


import org.insertcoin.productservice.entities.PlatformEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlatformRepository extends JpaRepository<PlatformEntity, Integer> {
    Optional<PlatformEntity> findByName(String name);
}
