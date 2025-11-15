package org.insertcoin.insertcoinpaymentservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.insertcoin.insertcoinpaymentservice.config.RabbitMQConfig;
import org.insertcoin.insertcoinpaymentservice.dtos.EmailMessageDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class EmailPublisher {
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public EmailPublisher(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    public void publish(EmailMessageDTO dto) {
        try {
            String json = objectMapper.writeValueAsString(dto);
            rabbitTemplate.convertAndSend(RabbitMQConfig.EMAIL_EXCHANGE, RabbitMQConfig.EMAIL_ROUTING_KEY, json);
            System.out.println("Published message for email-service: " + json);
        } catch (Exception e) {
            System.err.println("Failed to publish message for email-service: " + e.getMessage());
        }
    }
}
