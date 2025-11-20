package org.insertcoin.insertcoinorderservice.clients;

import org.insertcoin.insertcoinorderservice.dtos.response.CurrencyDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;

@FeignClient(name = "insertcoin-currency-service" , path = "/currency")
public interface CurrencyClient {

    @GetMapping("/{value}/{source}/{target}")
    CurrencyDTO convert(
            @PathVariable("value") BigDecimal value,
            @PathVariable("source") String source,
            @PathVariable("target") String target
    );
}
