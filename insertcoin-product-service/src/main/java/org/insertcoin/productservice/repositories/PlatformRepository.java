package org.insertcoin.productservice.repositories;


import org.insertcoin.productservice.entities.PlatformEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlatformRepository extends JpaRepository<PlatformEntity, Integer> {
    PlatformEntity findByName(String name);
}
