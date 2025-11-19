package org.insertcoin.productservice.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// ATENÇÃO: Você precisa ter essa classe AuthTokenFilter no projeto de produtos também
// Se ela estiver em outro pacote, ajuste o import abaixo.
// import org.insertcoin.productservice.components.AuthTokenFilter;

@Configuration
@EnableWebSecurity
public class ProductSecurityConfig {

    // Injetamos apenas o filtro de token, pois é ele que valida se o usuário pode entrar
    private final AuthTokenFilter authTokenFilter;

    public ProductSecurityConfig(AuthTokenFilter authTokenFilter) {
        this.authTokenFilter = authTokenFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                // APIs REST devem ser Stateless (sem sessão de servidor)
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        // ------------------------------------------------------------
                        // 1. ROTAS PÚBLICAS (Qualquer um acessa)
                        // ------------------------------------------------------------
                        // Swagger / OpenAPI (Documentação)
                        .requestMatchers(
                                "/products",
                                "/products",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        // Listar produtos e ver detalhes (Baseado na sua planilha)
                        .requestMatchers(HttpMethod.GET, "/products/**").permitAll()

                        // ------------------------------------------------------------
                        // 2. ROTAS PROTEGIDAS (Precisa de Token JWT)
                        // ------------------------------------------------------------
                        // Avaliar produto (POST /products/rating/{id})
                        .requestMatchers(HttpMethod.POST, "/products/rating/**").authenticated()

                        // Rotas Administrativas (ws/...)
                        // Nota: Se você tiver roles, troque .authenticated() por .hasRole("ADMIN")
                        .requestMatchers("/ws/**").authenticated()

                        // ------------------------------------------------------------
                        // 3. BLOQUEIO GERAL
                        // ------------------------------------------------------------
                        // Qualquer outra rota que não foi listada acima será bloqueada
                        .anyRequest().authenticated()
                )

                // Adiciona o seu filtro que valida o Token antes do filtro padrão do Spring
                .addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}