package org.insertcoin.insertcoin_auth_service.configs;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "insertcoin.email.exchange";
    public static final String ROUTING_KEY = "insertcoin.email.routingKey";

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }
}

