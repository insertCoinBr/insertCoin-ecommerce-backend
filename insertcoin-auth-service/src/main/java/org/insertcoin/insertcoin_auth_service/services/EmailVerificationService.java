package org.insertcoin.insertcoin_auth_service.services;

import jakarta.transaction.Transactional;
import org.insertcoin.insertcoin_auth_service.entities.EmailVerificationEntity;
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
    public String createOrUpdateVerification(String email, String code) {
        Optional<EmailVerificationEntity> existingOpt = repository.findByEmail(email);

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
        newVerification.setVerified(false);
        newVerification.setCreatedAt(LocalDateTime.now());
        newVerification.setExpiresAt(LocalDateTime.now().plusMinutes(10));

        repository.save(newVerification);
        return "CREATED";
    }


    public boolean validateCode(String email, String code) {
        Optional<EmailVerificationEntity> optional = repository.findByEmail(email);
        if (optional.isEmpty()) return false;

        EmailVerificationEntity verification = optional.get();

        if (verification.isVerified()) return false;
        if (verification.getExpiresAt().isBefore(LocalDateTime.now())) return false;
        if (!verification.getCode().equals(code)) return false;

        verification.setVerified(true);
        repository.save(verification);
        return true;
    }

    public boolean isVerified(String email) {
        return repository.findByEmail(email)
                .map(EmailVerificationEntity::isVerified)
                .orElse(false);
    }

    public boolean isEmailVerified(String email) {
        EmailVerificationEntity verification = repository.findByEmail(email)
                .orElse(null);

        return verification != null && verification.isVerified();
    }
}
