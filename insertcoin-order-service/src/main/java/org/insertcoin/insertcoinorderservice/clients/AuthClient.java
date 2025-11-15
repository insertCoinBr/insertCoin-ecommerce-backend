package org.insertcoin.insertcoinorderservice.clients;

import org.insertcoin.insertcoinorderservice.dtos.response.AuthMeResponseDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class AuthClient {

    private final WebClient webClient;

    public AuthClient(@Qualifier("gatewayWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public AuthMeResponseDTO getAuthenticatedUser(String token) {
        return webClient
                .get()
                .uri("/auth/me")
                .headers(headers -> headers.setBearerAuth(token))
                .retrieve()
                .bodyToMono(AuthMeResponseDTO.class)
                .block();
    }
}
