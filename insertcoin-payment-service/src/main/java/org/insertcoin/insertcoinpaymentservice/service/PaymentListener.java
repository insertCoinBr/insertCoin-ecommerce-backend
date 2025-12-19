package org.insertcoin.insertcoinpaymentservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.insertcoin.insertcoinpaymentservice.config.RabbitMQConfig;
import org.insertcoin.insertcoinpaymentservice.dtos.OrderMessageDTO;
import org.insertcoin.insertcoinpaymentservice.orchestrator.PaymentOrchestrator;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentListener {

    private final PaymentOrchestrator orchestrator;
    private final ObjectMapper mapper;

    public PaymentListener(PaymentOrchestrator orchestrator, ObjectMapper mapper) {
        this.orchestrator = orchestrator;
        this.mapper = mapper;
    }

    @RabbitListener(queues = RabbitMQConfig.ORDER_QUEUE)
    public void handleOrder(String json) throws Exception {
        OrderMessageDTO dto = mapper.readValue(json, OrderMessageDTO.class);
        orchestrator.process(dto);
    }
}
