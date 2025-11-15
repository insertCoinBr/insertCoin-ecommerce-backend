package org.insertcoin.insertcoinpaymentservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Fila que recebe pedidos
    public static final String ORDER_QUEUE = "insertcoin.payment.queue";
    public static final String ORDER_EXCHANGE = "insertcoin.payment.order.exchange";
    public static final String ORDER_ROUTING_KEY = "insertcoin.payment.order.routingKey";

    // Fila do email-service
    public static final String EMAIL_EXCHANGE = "insertcoin.email.exchange";
    public static final String EMAIL_ROUTING_KEY = "insertcoin.email.routingKey";

    @Bean
    public Queue orderQueue() {
        return new Queue(ORDER_QUEUE, true);
    }

    @Bean
    public TopicExchange orderExchange() {
        return new TopicExchange(ORDER_EXCHANGE);
    }

    @Bean
    public Binding orderBinding(Queue orderQueue, TopicExchange orderExchange) {
        return BindingBuilder.bind(orderQueue).to(orderExchange).with(ORDER_ROUTING_KEY);
    }

    @Bean
    public TopicExchange emailExchange() {
        return new TopicExchange(EMAIL_EXCHANGE);
    }
}
