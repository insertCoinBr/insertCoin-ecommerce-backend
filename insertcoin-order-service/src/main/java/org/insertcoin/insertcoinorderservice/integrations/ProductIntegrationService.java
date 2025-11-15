package org.insertcoin.insertcoinorderservice.integrations;

import org.insertcoin.insertcoinorderservice.dtos.response.ProductResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class ProductIntegrationService {

    private final WebClient webClient;
    private final String baseUrl;

    public ProductIntegrationService(WebClient webClient,
                                     @Value("${services.gateway.url}") String baseUrl) {
        this.webClient = webClient;
        this.baseUrl = baseUrl;
    }

    public ProductResponseDTO getProductById(String productId, String bearerToken) {
        return webClient.get()
                .uri(baseUrl + "/products/" + productId)
                .header(HttpHeaders.AUTHORIZATION, bearerToken)
                .retrieve()
                .bodyToMono(ProductResponseDTO.class)
                .block();
    }
}
