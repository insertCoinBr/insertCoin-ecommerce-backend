package org.insertcoin.insertcoinorderservice.dtos.response;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderAdminItemResponseDTO(
        UUID productId,
        String productName,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal,
        String imageUrl
) {}
