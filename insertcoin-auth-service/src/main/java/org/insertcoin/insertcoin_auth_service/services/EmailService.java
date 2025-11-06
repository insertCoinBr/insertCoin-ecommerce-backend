package org.insertcoin.insertcoin_auth_service.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(String to, String code) {
        String subject = "Verify your email address";
        String body = """
                <html>
                <body style="font-family: Arial, sans-serif;">
                    <h2>Welcome to InsertCoin 🎮</h2>
                    <p>Your verification code is:</p>
                    <h1 style="color:#4CAF50;">%s</h1>
                    <p>This code will expire in 10 minutes.</p>
                </body>
                </html>
                """.formatted(code);

        sendHtmlEmail(to, subject, body);
    }

    public void sendPasswordResetEmail(String to, String code) {
        String subject = "Reset your password";
        String body = """
                <html>
                <body style="font-family: Arial, sans-serif;">
                    <h2>Password Reset Request 🔐</h2>
                    <p>Use the code below to reset your password:</p>
                    <h1 style="color:#f44336;">%s</h1>
                    <p>If you did not request this, ignore this email.</p>
                </body>
                </html>
                """.formatted(code);

        sendHtmlEmail(to, subject, body);
    }

    private void sendHtmlEmail(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);

            mailSender.send(message);
            System.out.println("[EmailService] Email sent to " + to + " with subject: " + subject);
        } catch (Exception e) {
            System.err.println("[EmailService] Failed to send email: " + e.getMessage());
            throw new RuntimeException("Failed to send email");
        }
    }
}
