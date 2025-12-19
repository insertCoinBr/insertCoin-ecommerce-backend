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
import java.util.UUID;
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

        OrderEntity orderEntity = orderService.createOrder(authHeader, request);

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
                        item.getQuantity(),
                        item.getUnitPrice().multiply(conversionRateFinal).setScale(2, RoundingMode.HALF_UP).stripTrailingZeros(),
                        item.getSubtotal().multiply(conversionRateFinal).setScale(2, RoundingMode.HALF_UP).stripTrailingZeros(),
                        item.getImageUrl()
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

    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDTO> getOrderById(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable UUID orderId,
            @RequestParam(defaultValue = "BRL") String currency
    ) {

        OrderEntity orderEntity = orderService.getOrderById(authHeader, orderId);
        if (orderEntity == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        BigDecimal conversionRate = BigDecimal.ONE;
        if (!"BRL".equalsIgnoreCase(currency)) {
            conversionRate = orderService.getConversionRate("BRL", currency);
        }
        BigDecimal conversionRateFinal = conversionRate;

        List<OrderItemResponseDTO> items = orderEntity.getItems().stream()
                .map(item -> new OrderItemResponseDTO(
                        item.getProductId(),
                        item.getProductName(),
                        item.getQuantity(),
                        item.getUnitPrice().multiply(conversionRateFinal).setScale(2, RoundingMode.HALF_UP).stripTrailingZeros(),
                        item.getSubtotal().multiply(conversionRateFinal).setScale(2, RoundingMode.HALF_UP).stripTrailingZeros(),
                        item.getImageUrl()
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

        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("/user")
    public ResponseEntity<List<OrderResponseDTO>> getUserOrders(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(value = "currency", required = false, defaultValue = "BRL") String currency,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "orderBy", required = false, defaultValue = "createdAt") String orderBy,
            @RequestParam(value = "direction", required = false, defaultValue = "desc") String direction
    ) {

        List<OrderEntity> orders = orderService.getOrdersByUser(authHeader, currency, status, orderBy, direction);

        List<OrderResponseDTO> response = orders.stream().map(order -> {
            List<OrderItemResponseDTO> items = order.getItems().stream()
                    .map(item -> new OrderItemResponseDTO(
                            item.getProductId(),
                            item.getProductName(),
                            item.getQuantity(),
                            item.getUnitPrice(),
                            item.getSubtotal(),
                            item.getImageUrl()
                    )).collect(Collectors.toList());

            return new OrderResponseDTO(
                    order.getId(),
                    order.getOrderNumber(),
                    order.getTotalAmount(),
                    order.getStatus(),
                    order.getCreatedAt(),
                    items
            );
        }).collect(Collectors.toList());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
