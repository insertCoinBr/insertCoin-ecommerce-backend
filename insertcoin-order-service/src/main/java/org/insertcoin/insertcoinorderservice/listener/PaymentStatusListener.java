package org.insertcoin.insertcoinorderservice.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.insertcoin.insertcoinorderservice.dtos.events.PaymentStatusMessageDTO;
import org.insertcoin.insertcoinorderservice.orchestrator.OrderOrchestrationService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PaymentStatusListener {

    private final ObjectMapper objectMapper;
    private final OrderOrchestrationService orchestrationService;

    public PaymentStatusListener(ObjectMapper objectMapper,
                                 OrderOrchestrationService orchestrationService) {
        this.objectMapper = objectMapper;
        this.orchestrationService = orchestrationService;
    }

    @RabbitListener(queues = "insertcoin.payment.status.queue")
    public void handlePaymentStatus(String message) {
        try {
            PaymentStatusMessageDTO dto = objectMapper.readValue(message, PaymentStatusMessageDTO.class);

            if ("PAID".equalsIgnoreCase(dto.getStatus())) {
                orchestrationService.processPaidOrder(UUID.fromString(dto.getOrderId()));
            }

        } catch (Exception e) {
            System.err.println("Erro ao processar evento de pagamento: " + e.getMessage());
        }
    }
}

