package org.insertcoin.insertcoinorderservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${services.gateway.url}")
    private String gatewayServiceUrl;

    @Bean(name = "gatewayWebClient")
    public WebClient gatewayWebClient() {
        return WebClient.builder()
                .baseUrl(gatewayServiceUrl)
                .build();
    }
}
