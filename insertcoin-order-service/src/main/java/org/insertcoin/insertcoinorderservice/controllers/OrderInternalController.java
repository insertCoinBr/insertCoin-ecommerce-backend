package org.insertcoin.insertcoinorderservice.controllers;

import org.insertcoin.insertcoinorderservice.dtos.response.OrderNotificationDataDTO;
import org.insertcoin.insertcoinorderservice.entities.OrderEntity;
import org.insertcoin.insertcoinorderservice.repositories.OrderRepository;
import org.insertcoin.insertcoinorderservice.services.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/internal/orders")
public class OrderInternalController {

    private final OrderService orderService;

    public OrderInternalController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/{id}/notification-data")
    public ResponseEntity<OrderNotificationDataDTO> getNotificationData(
            @PathVariable UUID id
    ) {
        OrderNotificationDataDTO dto = orderService.getNotificationData(id);

        return ResponseEntity.ok(dto);
    }

}
