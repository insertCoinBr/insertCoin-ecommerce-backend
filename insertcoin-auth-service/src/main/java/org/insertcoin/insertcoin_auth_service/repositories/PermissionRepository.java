package org.insertcoin.insertcoin_auth_service.repositories;

import org.insertcoin.insertcoin_auth_service.entities.PermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PermissionRepository extends JpaRepository<PermissionEntity, UUID> {
}
