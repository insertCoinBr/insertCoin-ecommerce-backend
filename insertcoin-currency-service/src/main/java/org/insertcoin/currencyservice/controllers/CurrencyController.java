package org.insertcoin.currencyservice.controllers;

import org.insertcoin.currencyservice.clients.CurrencyBCClient;
import org.insertcoin.currencyservice.clients.CurrencyBCResponse;
import org.insertcoin.currencyservice.entities.CurrencyEntity;
import org.insertcoin.currencyservice.repositories.CurrencyRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/currency")
public class CurrencyController {

    private final CurrencyRepository repository;
    private final CurrencyBCClient bcClient;
    private final CacheManager cacheManager;

    @Value("${server.port}")
    private int serverPort;

    public CurrencyController(CurrencyRepository repository,
                              CurrencyBCClient bcClient,
                              CacheManager cacheManager) {
        this.repository = repository;
        this.bcClient = bcClient;
        this.cacheManager = cacheManager;
    }

    @GetMapping("/{value}/{source}/{target}")
    public ResponseEntity<CurrencyEntity> convert(
            @PathVariable double value,
            @PathVariable String source,
            @PathVariable String target) throws Exception {

        source = source.toUpperCase();
        target = target.toUpperCase();

        final String cacheName = "CurrencyCache";
        final String cacheKey = source + target;

        // 1. Verifica cache
        CurrencyEntity currency = cacheManager.getCache(cacheName).get(cacheKey, CurrencyEntity.class);
        String dataSource;

        if (currency != null) {
            dataSource = "Cache";
        } else {
            currency = new CurrencyEntity();
            currency.setSource(source);
            currency.setTarget(target);

            if (source.equals(target)) {
                currency.setConversionRate(1.0);
                dataSource = "Local Calculation";
            } else {
                try {
                    double sourceRate = getRateFromBC(source);
                    double targetRate = getRateFromBC(target);
                    currency.setConversionRate(sourceRate / targetRate);
                    dataSource = "API BCB";

                    var existingCurrency = repository.findBySourceAndTarget(source, target);
                    if (existingCurrency.isPresent()) {
                        currency.setId(existingCurrency.get().getId());
                    } else {
                        repository.save(currency);
                    }
                } catch (Exception e) {
                    currency = repository.findBySourceAndTarget(source, target)
                            .orElseThrow(() -> new Exception("Currency pair not found in local database"));
                    dataSource = "Local Database";
                }
            }

            cacheManager.getCache(cacheName).put(cacheKey, currency);
        }

        // 2. Calcula o valor convertido e ambiente
        currency.setConvertedValue(value * currency.getConversionRate());
        currency.setEnviroment("Currency service on port: " + serverPort + " - Source: " + dataSource);

        return ResponseEntity.ok(currency);
    }

    // -------------------
    // Métodos auxiliares
    // -------------------
    private double getRateFromBC(String currencyCode) throws Exception {
        if (currencyCode.equals("BRL")) {
            return 1.0;
        }

        CurrencyBCResponse response = bcClient.getCurrencyBC(currencyCode);
        if (response.getValue() == null || response.getValue().isEmpty()) {
            throw new Exception("No exchange data found for " + currencyCode);
        }

        // Usa a última cotação retornada
        return response.getValue()
                .getLast()
                .getCotacaoVenda();
    }
}
