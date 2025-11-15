package org.insertcoin.insertcoinorderservice.controllers;

import org.insertcoin.insertcoinorderservice.dtos.request.OrderCreateRequestDTO;
import org.insertcoin.insertcoinorderservice.dtos.response.OrderItemResponseDTO;
import org.insertcoin.insertcoinorderservice.dtos.response.OrderResponseDTO;
import org.insertcoin.insertcoinorderservice.entities.OrderEntity;
import org.insertcoin.insertcoinorderservice.services.EmailService;
import org.insertcoin.insertcoinorderservice.services.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final EmailService emailService;

    public OrderController(OrderService orderService, EmailService emailService) {
        this.orderService = orderService;
        this.emailService = emailService;
    }

    @PreAuthorize("hasRole('CLIENT')")
    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody OrderCreateRequestDTO request
    ) {
        // Extrair token Bearer
        String token = authHeader.replace("Bearer ", "");

        // Criar pedido
        OrderEntity orderEntity = orderService.createOrder(token, request);

        BigDecimal conversionRate = BigDecimal.ONE;
        if ("USD".equalsIgnoreCase(request.getCurrency())) {
            conversionRate = orderService.getConversionRate("BRL", "USD");
        }
        BigDecimal conversionRateFinal = conversionRate;

        BigDecimal totalAmountConverted = orderEntity.getTotalAmount()
                .multiply(conversionRate)
                .setScale(2, RoundingMode.HALF_UP);

        emailService.sendToQueuePaymentService(orderEntity, request, totalAmountConverted);

        // Mapear OrderEntity para OrderResponseDTO
        List<OrderItemResponseDTO> items = orderEntity.getItems().stream()
                .map(item -> new OrderItemResponseDTO(
                        item.getProductId(),
                        item.getProductName(),
                        item.getSku(),
                        item.getQuantity(),
                        item.getUnitPrice().multiply(conversionRateFinal).setScale(2, RoundingMode.HALF_UP).stripTrailingZeros(),
                        item.getSubtotal().multiply(conversionRateFinal).setScale(2, RoundingMode.HALF_UP).stripTrailingZeros()
                ))
                .collect(Collectors.toList());

        OrderResponseDTO responseDTO = new OrderResponseDTO(
                orderEntity.getId(),
                orderEntity.getOrderNumber(),
                orderEntity.getTotalAmount().multiply(conversionRate).setScale(2, RoundingMode.HALF_UP).stripTrailingZeros(),
                orderEntity.getStatus(),
                orderEntity.getCreatedAt(),
                items
        );

        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }
}
