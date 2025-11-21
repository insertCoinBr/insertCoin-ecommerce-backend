package org.insertcoin.gatewayservice.configs;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiGatewayConfig {

    @Bean
    RouteLocator getGatewayRouter(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(p -> p
                        .path("/auth/**")
                        .uri("lb://insertcoin-auth-service"))
                .route(p -> p
                        .path("/orders/**")
                        .uri("lb://insertcoin-order-service"))
                .route( p -> p
                        .path("/products/**")
                        .uri("lb://insertcoin-product-service")
                )
                .build();
    }
}
