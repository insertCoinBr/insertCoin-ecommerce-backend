package org.insertcoin.currencyservice.clients;

import org.springframework.stereotype.Component;
import java.util.Collections;

@Component
public class CurrencyBCFallback {

    public CurrencyBCResponse getCurrencyBC(String moeda) {
        CurrencyBCResponse currency = new CurrencyBCResponse();
        currency.setValue(Collections.emptyList());
        return currency;
    }
}
