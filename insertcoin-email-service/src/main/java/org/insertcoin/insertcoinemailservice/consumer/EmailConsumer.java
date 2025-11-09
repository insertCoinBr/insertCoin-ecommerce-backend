package org.insertcoin.insertcoinemailservice.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.insertcoin.insertcoinemailservice.service.TemplateEmailService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class EmailConsumer {

    private final TemplateEmailService templateEmailService;
    private final ObjectMapper objectMapper;

    public EmailConsumer(TemplateEmailService templateEmailService, ObjectMapper objectMapper) {
        this.templateEmailService = templateEmailService;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = "insertcoin.email.queue")
    public void receive(String json) {
        try {
            var node = objectMapper.readTree(json);
            String to = node.get("to").asText();
            String subject = node.get("subject").asText();
            String template = node.get("template").asText();

            Map<String, Object> variables = objectMapper.convertValue(
                    node.get("variables"),
                    new TypeReference<Map<String, Object>>() {}
            );

            templateEmailService.sendTemplateEmail(to, subject, template, variables);
        } catch (Exception e) {
            System.err.println("Failed to process incoming email message: " + e.getMessage());
        }
    }
}
