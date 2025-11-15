package org.insertcoin.insertcoinorderservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.insertcoin.insertcoinorderservice.config.RabbitMQConfig;
import org.insertcoin.insertcoinorderservice.dtos.events.OrderCreatedEventDTO;
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

    public void publish(OrderCreatedEventDTO dto) {
        try {
            String json = objectMapper.writeValueAsString(dto);
            rabbitTemplate.convertAndSend(RabbitMQConfig.PAYMENT_EXCHANGE, RabbitMQConfig.PAYMENT_ROUTING_KEY, json);
            System.out.println("Published message for payment-service: " + json);
        } catch (Exception e) {
            System.err.println("Failed to publish message for payment-service: " + e.getMessage());
        }
    }
}
