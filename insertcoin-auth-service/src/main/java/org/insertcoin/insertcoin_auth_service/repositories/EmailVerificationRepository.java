package org.insertcoin.insertcoin_auth_service.repositories;

import org.insertcoin.insertcoin_auth_service.entities.EmailVerificationEntity;
import org.insertcoin.insertcoin_auth_service.entities.VerificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmailVerificationRepository extends JpaRepository<EmailVerificationEntity, UUID> {
    Optional<EmailVerificationEntity> findByEmailAndType(String email, VerificationType type);
}
