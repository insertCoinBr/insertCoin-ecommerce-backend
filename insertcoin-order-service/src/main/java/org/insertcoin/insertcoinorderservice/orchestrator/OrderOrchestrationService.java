package org.insertcoin.insertcoinorderservice.orchestrator;

import jakarta.transaction.Transactional;
import org.insertcoin.insertcoinorderservice.entities.OrderEntity;
import org.insertcoin.insertcoinorderservice.enums.OrderStatus;
import org.insertcoin.insertcoinorderservice.repositories.OrderRepository;
import org.insertcoin.insertcoinorderservice.services.KeyDeliveryService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrderOrchestrationService {

    private final OrderRepository orderRepository;
    private final KeyDeliveryService keyDeliveryService;

    public OrderOrchestrationService(OrderRepository orderRepository,
                                     KeyDeliveryService keyDeliveryService) {
        this.orderRepository = orderRepository;
        this.keyDeliveryService = keyDeliveryService;
    }

    @Transactional
    public void processPaidOrder(UUID orderId) {
        OrderEntity order = orderRepository.findByIdWithItems(orderId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado: " + orderId));

        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);

        keyDeliveryService.deliverKeys(order);
    }
}

