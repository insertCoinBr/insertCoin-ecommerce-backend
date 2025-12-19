package org.insertcoin.insertcoinpaymentservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.insertcoin.insertcoinpaymentservice.config.RabbitMQConfig;
import org.insertcoin.insertcoinpaymentservice.dtos.PaymentStatusDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class PaymentStatusPublisher {
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public PaymentStatusPublisher(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    public void publish(PaymentStatusDTO dto) {
        try {
            String json = objectMapper.writeValueAsString(dto);
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.PAYMENT_EXCHANGE,
                    RabbitMQConfig.PAYMENT_STATUS_ROUTING_KEY,
                    json
            );
            System.out.println("Published payment status message: " + json);
        } catch (Exception e) {
            System.err.println("Failed to publish payment status message: " + e.getMessage());
        }
    }
}
