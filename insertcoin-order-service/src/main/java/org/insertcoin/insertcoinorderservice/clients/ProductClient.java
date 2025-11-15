package org.insertcoin.insertcoinorderservice.clients;

import org.insertcoin.insertcoinorderservice.dtos.response.ProductResponseDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.UUID;

@Component
public class ProductClient {

    private final WebClient webClient;

    public ProductClient(@Qualifier("gatewayWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public ProductResponseDTO findById(UUID id, String token) {
        return webClient
                .get()
                .uri("/products/" + id)
                .headers(headers -> headers.setBearerAuth(token))
                .retrieve()
                .bodyToMono(ProductResponseDTO.class)
                .block();
    }
}
