package org.insertcoin.insertcoinpaymentservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.WriterException;
import org.insertcoin.insertcoinpaymentservice.config.RabbitMQConfig;
import org.insertcoin.insertcoinpaymentservice.publisher.EmailPublisher;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class PaymentListener {

    private final PaymentService paymentService;
    private final EmailService emailService;
    private final ObjectMapper objectMapper;

    public PaymentListener(PaymentService paymentService, RabbitTemplate rabbitTemplate, EmailPublisher emailPublisher, EmailService emailService, ObjectMapper objectMapper) {
        this.paymentService = paymentService;
        this.emailService = emailService;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = RabbitMQConfig.ORDER_QUEUE)
    public void handleOrderCreated(String json) throws IOException, WriterException {

        try {
            var node = objectMapper.readTree(json);
            String orderId = node.get("orderId").asText();
            double amount = node.get("amount").asDouble();
            String customerEmail = node.get("customerEmail").asText();
            String paymentMethod = node.get("paymentMethod").asText();

            if("PIX".equalsIgnoreCase(paymentMethod)) {
                String qrCode = paymentService.generatePixQrCode(orderId, amount);
                emailService.buildPixEmail(orderId, customerEmail, qrCode, amount);
            } else {
                emailService.buildCardEmail(orderId, customerEmail, amount);
            }

        } catch (Exception e) {
            System.err.println("Failed to process incoming email message: " + e.getMessage());
        }
    }
}

