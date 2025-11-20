package org.insertcoin.insertcoinorderservice.controllers;

import org.insertcoin.insertcoinorderservice.dtos.response.OrderAdminFullResponseDTO;
import org.insertcoin.insertcoinorderservice.dtos.response.OrderAdminItemResponseDTO;
import org.insertcoin.insertcoinorderservice.dtos.response.OrderAdminSearchResponseDTO;
import org.insertcoin.insertcoinorderservice.entities.OrderEntity;
import org.insertcoin.insertcoinorderservice.entities.PaymentEntity;
import org.insertcoin.insertcoinorderservice.enums.OrderStatus;
import org.insertcoin.insertcoinorderservice.services.CurrencyConversionService;
import org.insertcoin.insertcoinorderservice.services.OrderService;
import org.insertcoin.insertcoinorderservice.services.PaymentService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/orders/admin")
public class OrderAdminController {

    private final OrderService orderService;
    private final PaymentService paymentService;
    private final CurrencyConversionService currencyConversionService;

    public OrderAdminController(OrderService orderService, PaymentService paymentService, CurrencyConversionService currencyConversionService) {
        this.orderService = orderService;
        this.paymentService = paymentService;
        this.currencyConversionService = currencyConversionService;
    }

    @PreAuthorize("hasAuthority('ORDERS_ADMIN')")
    @DeleteMapping("/deleteOrder/{orderId}")
    public ResponseEntity<?> deleteOrder(@PathVariable UUID orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('ORDERS_ADMIN')")
    @GetMapping("/searchOrder")
    public ResponseEntity<?> searchOrders(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String orderNumber,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        OrderStatus statusEnum = null;

        if (status != null && !status.isBlank()) {
            statusEnum = OrderStatus.valueOf(status.toUpperCase());
        }

        Page<OrderEntity> orders = orderService.searchOrders(statusEnum, orderNumber, page, size);

        Page<OrderAdminSearchResponseDTO> response = orders.map(order ->
                new OrderAdminSearchResponseDTO(
                        order.getId(),
                        order.getOrderNumber(),
                        order.getStatus().name(),
                        order.getCreatedAt()
                )
        );

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('ORDERS_ADMIN')")
    @GetMapping("/dataOrder/{orderId}")
    public ResponseEntity<?> getOrderAdmin(
            @PathVariable UUID orderId,
            @RequestParam(defaultValue = "BRL") String currency
    ) {
        OrderEntity order = orderService.findByIdOrThrow(orderId);

        BigDecimal convertedTotal = currencyConversionService.convertUSDToBRL(order.getTotalAmount(), currency);

        PaymentEntity payment = paymentService.getPaymentByOrderId(orderId);

        String paymentMethod = (payment != null)
                ? payment.getPaymentMethod()
                : "UNKNOWN";

        List<OrderAdminItemResponseDTO> itemResponses = order.getItems().stream()
                .map(item -> new OrderAdminItemResponseDTO(
                        item.getProductId(),
                        item.getProductName(),
                        item.getSku(),
                        item.getQuantity(),
                        currencyConversionService.convertUSDToBRL(item.getUnitPrice(), currency),
                        currencyConversionService.convertUSDToBRL(item.getSubtotal(), currency),
                        item.getImageUrl()
                )).toList();

        OrderAdminFullResponseDTO response = new OrderAdminFullResponseDTO(
                order.getId(),
                order.getOrderNumber(),
                order.getStatus().name(),
                order.getCreatedAt(),
                order.getCustomerName(),
                order.getCustomerEmail(),
                convertedTotal,
                currency.toUpperCase(),
                paymentMethod,
                itemResponses
        );

        return ResponseEntity.ok(response);
    }

}
