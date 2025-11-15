package org.insertcoin.insertcoinpaymentservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.WriterException;
import org.insertcoin.insertcoinpaymentservice.config.RabbitMQConfig;
import org.insertcoin.insertcoinpaymentservice.dtos.ProductDTO;
import org.insertcoin.insertcoinpaymentservice.publisher.EmailPublisher;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
            String currency = node.get("currency").asText();

            if("PIX".equalsIgnoreCase(paymentMethod)) {
                String qrCode = paymentService.generatePixQrCode(orderId, amount);
                emailService.buildPixEmail(orderId, customerEmail, qrCode, amount);
            } else if(("CARD".equalsIgnoreCase(paymentMethod))){
                var cardNode = node.get("card");
                if (cardNode == null) {
                    throw new RuntimeException("Cartão não informado para pagamento com cartão.");
                }
                String number = cardNode.get("number").asText();
                String holderName = cardNode.get("holderName").asText();
                int expiryMonth = cardNode.get("expiryMonth").asInt();
                int expiryYear = cardNode.get("expiryYear").asInt();
                String cvv = cardNode.get("cvv").asText();

                paymentService.validateCard(number, holderName, expiryMonth, expiryYear, cvv);

                var productsNode = node.get("products");
                List<ProductDTO> products = new ArrayList<>();
                if (productsNode != null && productsNode.isArray()) {
                    for (var p : productsNode) {
                        String productName = p.get("productName").asText();
                        int quantity = p.get("quantity").asInt();
                        products.add(new ProductDTO(productName, quantity));
                    }
                }

                emailService.buildCardEmail(orderId, customerEmail, amount, products, currency);
            } else {
                System.err.println("Unsupported payment method: " + paymentMethod);
            }

        } catch (Exception e) {
            System.err.println("Failed to process incoming email message: " + e.getMessage());
        }
    }
}

