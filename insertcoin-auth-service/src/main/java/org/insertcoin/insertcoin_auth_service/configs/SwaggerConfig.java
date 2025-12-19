package org.insertcoin.insertcoin_auth_service.configs;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI insertCoinOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("InsertCoin Auth Service API")
                        .description("APIs de autenticação e gerenciamento de usuários da plataforma InsertCoin.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Equipe InsertCoin")
                                .email("insertcoin.app@gmail.com")
                        )
                );
    }
}
