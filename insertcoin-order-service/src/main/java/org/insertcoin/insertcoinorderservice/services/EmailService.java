package org.insertcoin.insertcoinorderservice.services;

import org.insertcoin.insertcoinorderservice.dtos.events.OrderCreatedEventDTO;
import org.insertcoin.insertcoinorderservice.dtos.request.OrderCreateRequestDTO;
import org.insertcoin.insertcoinorderservice.entities.OrderEntity;
import org.insertcoin.insertcoinorderservice.publisher.EmailPublisher;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final EmailPublisher publisher;

    public EmailService(EmailPublisher publisher) {
        this.publisher = publisher;
    }

    public void sendToQueuePaymentService(OrderEntity savedOrder, OrderCreateRequestDTO request) {
        OrderCreatedEventDTO dto = new OrderCreatedEventDTO();
        dto.setOrderId(savedOrder.getId().toString());
        dto.setAmount(savedOrder.getTotalAmount());
        dto.setCustomerEmail(savedOrder.getCustomerEmail());
        dto.setPaymentMethod(request.getPaymentMethod());
        publisher.publish(dto);
    }
}
