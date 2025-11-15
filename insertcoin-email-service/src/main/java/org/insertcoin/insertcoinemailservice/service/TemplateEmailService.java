package org.insertcoin.insertcoinemailservice.service;

import org.insertcoin.insertcoinemailservice.dtos.EmailAttachmentDTO;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.springframework.mail.javamail.MimeMessageHelper;
import jakarta.mail.internet.MimeMessage;
import org.springframework.stereotype.Service;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
public class TemplateEmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public TemplateEmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    public void sendTemplateEmail(String to, String subject, String templateName, Map<String, Object> variables, List<EmailAttachmentDTO> attachments) {
        try {
            Context context = new Context();
            context.setVariables(variables);
            String html = templateEngine.process(templateName, context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            helper.setFrom("insertcoin.app@gmail.com");

            if (attachments != null && !attachments.isEmpty()) {
                for (EmailAttachmentDTO attachment : attachments) {
                    if (attachment.isInline()) {
                        byte[] bytes = Base64.getDecoder().decode(attachment.getContent());
                        helper.addInline(attachment.getId(), new ByteArrayResource(bytes), attachment.getType());
                    }
                }
            }

            mailSender.send(message);
            System.out.println("Email sent to " + to + " using template " + templateName);
        } catch (Exception e) {
            System.err.println("Failed to send templated email: " + e.getMessage());
        }
    }
}
