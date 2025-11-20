package org.insertcoin.insertcoinorderservice.dtos.response;

import java.math.BigDecimal;

public record CurrencyDTO(
        BigDecimal conversionRate,
        BigDecimal convertedValue
) {}

