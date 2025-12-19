package org.insertcoin.insertcoinorderservice.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String PAYMENT_QUEUE = "insertcoin.payment.queue";
    public static final String PAYMENT_EXCHANGE = "insertcoin.payment.exchange";
    public static final String PAYMENT_ROUTING_KEY = "insertcoin.payment.routingKey";
    public static final String PAYMENT_STATUS_QUEUE = "insertcoin.payment.status.queue";

    // Fila do email-service
    public static final String EMAIL_EXCHANGE = "insertcoin.email.exchange";
    public static final String EMAIL_ROUTING_KEY = "insertcoin.email.routingKey";

    @Bean
    public Queue paymentQueue() {
        return new Queue(PAYMENT_QUEUE, true);
    }

    @Bean
    public TopicExchange emailExchange() {
        return new TopicExchange(EMAIL_EXCHANGE);
    }

    @Bean
    public TopicExchange paymentExchange() {
        return new TopicExchange(PAYMENT_EXCHANGE);
    }

    @Bean
    public Binding binding(Queue paymentQueue, TopicExchange paymentExchange) {
        return BindingBuilder.bind(paymentQueue).to(paymentExchange).with(PAYMENT_ROUTING_KEY);
    }

    @Bean
    public Queue paymentStatusQueue() {
        return new Queue(PAYMENT_STATUS_QUEUE, true);
    }
}
