package org.insertcoin.insertcoinorderservice.integrations;

import org.insertcoin.insertcoinorderservice.dtos.response.AuthMeResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class AuthIntegrationService {

    private final WebClient webClient;
    private final String baseUrl;

    public AuthIntegrationService(WebClient webClient,
                                  @Value("${services.gateway.url}") String baseUrl) {
        this.webClient = webClient;
        this.baseUrl = baseUrl;
    }

    public AuthMeResponseDTO getCurrentUser(String bearerToken) {
        return webClient.get()
                .uri(baseUrl + "/auth/me")
                .header(HttpHeaders.AUTHORIZATION, bearerToken)
                .retrieve()
                .bodyToMono(AuthMeResponseDTO.class)
                .block();
    }
}
