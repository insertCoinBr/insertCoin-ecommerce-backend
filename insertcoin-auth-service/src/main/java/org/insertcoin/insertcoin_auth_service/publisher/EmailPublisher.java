package org.insertcoin.insertcoin_auth_service.publisher;

import org.insertcoin.insertcoin_auth_service.configs.RabbitMQConfig;
import org.insertcoin.insertcoin_auth_service.dtos.EmailMessageDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

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
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.ROUTING_KEY, json);
            System.out.println("Published email message: " + json);
        } catch (Exception e) {
            System.err.println("Failed to publish email message: " + e.getMessage());
        }
    }
}
