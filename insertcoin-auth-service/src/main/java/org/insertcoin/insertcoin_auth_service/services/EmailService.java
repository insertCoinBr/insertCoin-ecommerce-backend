package org.insertcoin.insertcoin_auth_service.services;

import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import jakarta.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(String to, String code) {
        String subject = "Verify your email address - InsertCoin";
        String body = loadTemplate("templates/email/email-verification.html", code);
        sendHtmlEmail(to, subject, body);
    }

    public void sendPasswordResetEmail(String to, String code) {
        String subject = "Reset your password - InsertCoin";
        String body = loadTemplate("templates/email/password-reset.html", code);
        sendHtmlEmail(to, subject, body);
    }

    private String loadTemplate(String templatePath, String code) {
        try {
            ClassPathResource resource = new ClassPathResource(templatePath);
            String template = StreamUtils.copyToString(
                    resource.getInputStream(),
                    StandardCharsets.UTF_8
            );

            return template.replace("${code}", code);

        } catch (Exception e) {
            System.err.println("[EmailService] Failed to load template: " + e.getMessage());
            throw new RuntimeException("Failed to load email template", e);
        }
    }

    private void sendHtmlEmail(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);

            mailSender.send(message);
            System.out.println("[EmailService] Email sent to " + to + " with subject: " + subject);
        } catch (Exception e) {
            System.err.println("[EmailService] Failed to send email: " + e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
