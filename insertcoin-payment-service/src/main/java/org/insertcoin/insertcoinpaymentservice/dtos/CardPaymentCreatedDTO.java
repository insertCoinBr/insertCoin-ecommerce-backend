package org.insertcoin.insertcoinpaymentservice.dtos;

import java.math.BigDecimal;
import java.util.UUID;

public record CardPaymentCreatedDTO(
        UUID orderId,
        String orderNumber,
        String customerEmail,
        String transactionId,
        BigDecimal amount
) {}

