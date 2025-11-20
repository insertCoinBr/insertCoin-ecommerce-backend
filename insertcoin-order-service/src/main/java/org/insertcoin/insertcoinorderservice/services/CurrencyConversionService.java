package org.insertcoin.insertcoinorderservice.services;

import org.insertcoin.insertcoinorderservice.clients.CurrencyClient;
import org.insertcoin.insertcoinorderservice.dtos.response.CurrencyDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CurrencyConversionService {

    private final CurrencyClient currencyClient;

    public CurrencyConversionService(CurrencyClient currencyClient) {
        this.currencyClient = currencyClient;
    }

    public BigDecimal convertUSDToBRL(BigDecimal value, String currency) {
        if ("BRL".equalsIgnoreCase(currency)) {
            return value;
        }

        CurrencyDTO response = currencyClient.convert(value, "BRL", "USD");
        return response.convertedValue();
    }
}

