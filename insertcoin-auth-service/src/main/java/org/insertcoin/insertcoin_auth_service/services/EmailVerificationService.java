package org.insertcoin.insertcoin_auth_service.services;

import jakarta.transaction.Transactional;
import org.insertcoin.insertcoin_auth_service.entities.EmailVerificationEntity;
import org.insertcoin.insertcoin_auth_service.entities.VerificationType;
import org.insertcoin.insertcoin_auth_service.repositories.EmailVerificationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class EmailVerificationService {

    private final EmailVerificationRepository repository;

    public EmailVerificationService(EmailVerificationRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public String createOrUpdateVerification(String email, String code, VerificationType type) {
        Optional<EmailVerificationEntity> existingOpt = repository.findByEmailAndType(email, type);

        if (existingOpt.isPresent()) {
            EmailVerificationEntity existing = existingOpt.get();

            if (existing.getCreatedAt() != null &&
                    existing.getCreatedAt().isAfter(LocalDateTime.now().minusMinutes(1))) {
                return "WAIT";
            }

            existing.setCode(code);
            existing.setVerified(false);
            existing.setCreatedAt(LocalDateTime.now());
            existing.setExpiresAt(LocalDateTime.now().plusMinutes(10));
            repository.save(existing);
            return "UPDATED";
        }

        EmailVerificationEntity newVerification = new EmailVerificationEntity();
        newVerification.setEmail(email);
        newVerification.setCode(code);
        newVerification.setType(type);
        newVerification.setVerified(false);
        newVerification.setCreatedAt(LocalDateTime.now());
        newVerification.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        repository.save(newVerification);
        return "CREATED";
    }

    @Transactional
    public boolean validateCode(String email, String code, VerificationType type) {
        Optional<EmailVerificationEntity> opt = repository.findByEmailAndType(email, type);
        if (opt.isEmpty()) return false;

        EmailVerificationEntity entity = opt.get();

        if (!entity.getCode().equals(code)) return false;
        if (entity.getExpiresAt().isBefore(LocalDateTime.now())) return false;

        entity.setVerified(true);
        repository.save(entity);
        return true;
    }

    public boolean isVerified(String email, VerificationType type) {
        return repository.findByEmailAndType(email, type)
                .map(EmailVerificationEntity::isVerified)
                .orElse(false);
    }
}
