package org.insertcoin.insertcoinorderservice.controllers;

import org.insertcoin.insertcoinorderservice.dtos.request.OrderCreateRequestDTO;
import org.insertcoin.insertcoinorderservice.dtos.response.OrderItemResponseDTO;
import org.insertcoin.insertcoinorderservice.dtos.response.OrderResponseDTO;
import org.insertcoin.insertcoinorderservice.entities.OrderEntity;
import org.insertcoin.insertcoinorderservice.entities.OrderItemEntity;
import org.insertcoin.insertcoinorderservice.services.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
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

        // Mapear OrderEntity para OrderResponseDTO
        List<OrderItemResponseDTO> items = orderEntity.getItems().stream()
                .map(item -> new OrderItemResponseDTO(
                        item.getProductId(),
                        item.getProductName(),
                        item.getSku(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getSubtotal()
                ))
                .collect(Collectors.toList());

        OrderResponseDTO responseDTO = new OrderResponseDTO(
                orderEntity.getId(),
                orderEntity.getOrderNumber(),
                orderEntity.getTotalAmount(),
                orderEntity.getStatus(),
                orderEntity.getCreatedAt(),
                items
        );

        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }
}
