package org.insertcoin.insertcoinorderservice.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.insertcoin.insertcoinorderservice.dtos.events.PaymentStatusMessageDTO;
import org.insertcoin.insertcoinorderservice.entities.OrderEntity;
import org.insertcoin.insertcoinorderservice.enums.OrderStatus;
import org.insertcoin.insertcoinorderservice.repositories.OrderRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PaymentStatusListener {

    private final OrderRepository orderRepository;
    private final ObjectMapper objectMapper;

    public PaymentStatusListener(OrderRepository orderRepository, ObjectMapper objectMapper) {
        this.orderRepository = orderRepository;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = "insertcoin.payment.status.queue")
    public void handlePaymentStatus(String message) {
        try {
            PaymentStatusMessageDTO dto = objectMapper.readValue(message, PaymentStatusMessageDTO.class);

            UUID orderId = UUID.fromString(dto.getOrderId());

            OrderEntity order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Pedido não encontrado: " + dto.getOrderId()));

            order.setStatus(OrderStatus.valueOf(dto.getStatus()));
            orderRepository.save(order);

            System.out.println("Pedido atualizado pelo payment-service: " + dto.getOrderId() + " -> " + dto.getStatus());

        } catch (Exception e) {
            System.err.println("Erro ao processar mensagem de payment-service: " + e.getMessage());
        }
    }
}
