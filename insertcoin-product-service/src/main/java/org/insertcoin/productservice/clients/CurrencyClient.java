package org.insertcoin.productservice.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "insertcoin-currency-service", path = "/currency")
public interface CurrencyClient {

    @GetMapping("/{value}/{source}/{target}")
    CurrencyResponse convert(
            @PathVariable double value,
            @PathVariable String source,
            @PathVariable String target
    );
}
