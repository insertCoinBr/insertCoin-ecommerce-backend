package org.insertcoin.currencyservice.clients;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class CurrencyBCClient {

    private static final Logger log = LoggerFactory.getLogger(CurrencyBCClient.class);
    private static final String BASE_URL = "https://olinda.bcb.gov.br/olinda/servico/PTAX/versao/v1/odata";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MM-dd-yyyy");

    private final RestTemplate restTemplate;
    private final CurrencyBCFallback fallback;

    public CurrencyBCClient(RestTemplate restTemplate, CurrencyBCFallback fallback) {
        this.restTemplate = restTemplate;
        this.fallback = fallback;
    }

    public CurrencyBCResponse getCurrencyBC(String moeda) {
        try {
            String data = LocalDate.now().format(FORMATTER);
            String url = String.format(
                    "%s/CotacaoMoedaDia(moeda=@moeda,dataCotacao=@dataCotacao)?@moeda='%s'&@dataCotacao='%s'&$format=json",
                    BASE_URL,
                    moeda,
                    data
            );

            log.info("Calling BC API for currency: {}", moeda);
            return restTemplate.getForObject(url, CurrencyBCResponse.class);

        } catch (RestClientException e) {
            log.error("Error calling BC API, using fallback. Error: {}", e.getMessage());
            return fallback.getCurrencyBC(moeda);
        }
    }
}
