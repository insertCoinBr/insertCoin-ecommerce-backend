package org.insertcoin.insertcoin_auth_service.services;

import org.insertcoin.insertcoin_auth_service.dtos.EmailMessageDTO;
import org.insertcoin.insertcoin_auth_service.publisher.EmailPublisher;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class EmailService {

    private final EmailPublisher publisher;

    public EmailService(EmailPublisher publisher) {
        this.publisher = publisher;
    }

    public void sendVerificationEmail(String to, String code) {
        EmailMessageDTO dto = new EmailMessageDTO();
        dto.setType("VERIFICATION");
        dto.setTo(to);
        dto.setSubject("Verify your email address - InsertCoin");
        dto.setTemplate("email-verification");
        dto.setVariables(Map.of("code", code));
        publisher.publish(dto);
    }

    public void sendPasswordResetEmail(String to, String code) {
        EmailMessageDTO dto = new EmailMessageDTO();
        dto.setType("PASSWORD_RESET");
        dto.setTo(to);
        dto.setSubject("Reset your password - InsertCoin");
        dto.setTemplate("password-reset");
        dto.setVariables(Map.of("code", code));
        publisher.publish(dto);
    }
}
