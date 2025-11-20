package org.insertcoin.insertcoinorderservice.dtos.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderAdminFullResponseDTO(
        UUID orderId,
        String orderNumber,
        String status,
        LocalDateTime orderDate,
        String customerName,
        String customerEmail,
        BigDecimal totalAmount,
        String currency,
        String paymentMethod,
        List<OrderAdminItemResponseDTO> items
) {}

