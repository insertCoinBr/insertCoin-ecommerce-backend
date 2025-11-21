package org.insertcoin.insertcoinorderservice.services;

import org.insertcoin.insertcoinorderservice.dtos.events.OrderCreatedEventDTO;
import org.insertcoin.insertcoinorderservice.dtos.request.OrderCreateRequestDTO;
import org.insertcoin.insertcoinorderservice.entities.OrderEntity;
import org.insertcoin.insertcoinorderservice.publisher.EmailPublisher;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

@Service
public class EmailService {

    private final EmailPublisher publisher;

    public EmailService(EmailPublisher publisher) {
        this.publisher = publisher;
    }

    public void sendToQueuePaymentService(OrderEntity savedOrder, OrderCreateRequestDTO request, BigDecimal totalAmount) {
        OrderCreatedEventDTO dto = new OrderCreatedEventDTO();
        dto.setOrderId(savedOrder.getId());
        dto.setOrderNumber(savedOrder.getOrderNumber());
        dto.setAmount(totalAmount);
        dto.setCustomerEmail(savedOrder.getCustomerEmail());
        dto.setPaymentMethod(request.getPaymentMethod());
        dto.setCurrency(request.getCurrency());

        if ("CARD".equalsIgnoreCase(request.getPaymentMethod()) && request.getCard() != null) {
            OrderCreatedEventDTO.CardDTO cardDTO = new OrderCreatedEventDTO.CardDTO();
            cardDTO.setNumber(request.getCard().getNumber());
            cardDTO.setHolderName(request.getCard().getHolderName());
            cardDTO.setExpiryMonth(request.getCard().getExpiryMonth());
            cardDTO.setExpiryYear(request.getCard().getExpiryYear());
            cardDTO.setCvv(request.getCard().getCvv());

            dto.setCard(cardDTO);
        }

        List<OrderCreatedEventDTO.ProductDTO> products = savedOrder.getItems().stream()
                .map(item -> new OrderCreatedEventDTO.ProductDTO(
                        item.getProductName(),
                        item.getQuantity()
                ))
                .toList();
        dto.setProducts(products);

        publisher.publish(dto);
    }
}
