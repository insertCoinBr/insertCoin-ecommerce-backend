package org.insertcoin.productservice.clients;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class CurrencyClient {

    private static final Logger log = LoggerFactory.getLogger(CurrencyClient.class);
    private static final String BASE_URL = "http://localhost:8100/currency";

    private final RestTemplate restTemplate;

    public CurrencyClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public CurrencyResponse convert(double value, String source, String target) {
        try {
            // força formato com ponto, independentemente do locale do SO
            String url = String.format(java.util.Locale.US, "%s/%.2f/%s/%s", BASE_URL, value, source, target);
            log.info("Requesting currency conversion: {}", url);
            return restTemplate.getForObject(url, CurrencyResponse.class);
        } catch (Exception e) {
            log.error("Currency service unavailable. Error: {}", e.getMessage());
            return null;
        }
    }

}

