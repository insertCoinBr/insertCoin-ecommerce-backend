package org.insertcoin.insertcoinorderservice.services;

import org.insertcoin.insertcoinorderservice.dtos.events.OrderCreatedEventDTO;
import org.insertcoin.insertcoinorderservice.dtos.request.OrderCreateRequestDTO;
import org.insertcoin.insertcoinorderservice.dtos.response.AssignedKeysResponseDTO;
import org.insertcoin.insertcoinorderservice.dtos.response.EmailMessageDTO;
import org.insertcoin.insertcoinorderservice.entities.OrderEntity;
import org.insertcoin.insertcoinorderservice.publisher.EmailPublisher;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        publisher.publishToPayment(dto);
    }

    public void buildDeliveryKeysEmail(
            String customerEmail,
            String orderNumber,
            AssignedKeysResponseDTO keysResponse
    ) {

        EmailMessageDTO email = new EmailMessageDTO();
        email.setType("GAME_KEYS_DELIVERY");
        email.setTo(customerEmail);
        email.setSubject("Suas chaves de jogos - Pedido " + orderNumber);
        email.setTemplate("game-keys-delivery");

        Map<String, Object> variables = new HashMap<>();
        variables.put("orderId", orderNumber);
        variables.put("products", keysResponse.getItems());

        email.setVariables(variables);
        email.setAttachments(Collections.emptyList());
        publisher.publishToEmail(email);
    }
}
