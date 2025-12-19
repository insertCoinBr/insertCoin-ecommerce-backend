package org.insertcoin.gatewayservice.filters;

import io.jsonwebtoken.Claims;
import org.insertcoin.gatewayservice.components.JwtUtil;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Component
public class AuthFilter implements GlobalFilter, Ordered {

    private static final List<String> PROTECTED_ROUTES = List.of("/ws/");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        var request = exchange.getRequest();
        String path = request.getURI().getPath();

        if (PROTECTED_ROUTES.stream().noneMatch(path::startsWith)) {
            return chain.filter(exchange);
        }

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);
            Claims payload = JwtUtil.validateToken(jwt);

            if (payload != null) {
                Object idObj = payload.get("id");
                String userId = idObj != null ? String.valueOf(idObj) : "";

                List<String> rolesList = new ArrayList<>();
                Object rolesObj = payload.get("roles");
                if (rolesObj instanceof List<?>) {
                    for (Object role : (List<?>) rolesObj) {
                        if (role instanceof String) {
                            rolesList.add((String) role);
                        }
                    }
                }

                ServerHttpRequest modifiedRequest = request.mutate()
                        .header("X-User-Id", userId)
                        .header("X-User-Roles", String.join(",", rolesList))
                        .build();

                return chain.filter(exchange.mutate().request(modifiedRequest).build());
            }
        }

        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
