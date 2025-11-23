package org.insertcoin.insertcoinorderservice.services;

import feign.FeignException;
import org.insertcoin.insertcoinorderservice.clients.ProductClient;
import org.insertcoin.insertcoinorderservice.dtos.request.AssignProductKeysItemDTO;
import org.insertcoin.insertcoinorderservice.dtos.request.AssignProductKeysRequestDTO;
import org.insertcoin.insertcoinorderservice.dtos.response.AssignedKeysResponseDTO;
import org.insertcoin.insertcoinorderservice.entities.OrderEntity;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;

@Service
public class KeyDeliveryService {

    private final ProductClient productClient;
    private final EmailService emailService;

    public KeyDeliveryService(ProductClient productClient, EmailService emailService) {
        this.productClient = productClient;
        this.emailService = emailService;
    }

    public void deliverKeys(OrderEntity order) {

        var request = new AssignProductKeysRequestDTO();
        request.setOrderId(order.getId());

        request.setItems(
                order.getItems().stream()
                        .map(item -> {
                            var dto = new AssignProductKeysItemDTO();
                            dto.setProductId(item.getProductId());
                            dto.setQuantity(item.getQuantity());
                            return dto;
                        })
                        .collect(Collectors.toList())
        );

        try {
            AssignedKeysResponseDTO keysResponse = productClient.assignKeys(request);

            emailService.buildDeliveryKeysEmail(
                    order.getCustomerEmail(),
                    order.getOrderNumber(),
                    keysResponse
            );
            System.out.println("Chaves enviadas com sucesso ao serviço de email para " + order.getCustomerEmail());

        } catch (FeignException e) {
            String body = e.contentUTF8();
            System.err.println("Status: " + e.status());
            System.err.println("Response body: " + body);

            // Desenvolver estorno e email
        }
    }
}

