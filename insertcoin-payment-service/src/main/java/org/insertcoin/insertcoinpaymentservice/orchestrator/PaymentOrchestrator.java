package org.insertcoin.insertcoinpaymentservice.orchestrator;

import org.insertcoin.insertcoinpaymentservice.dtos.OrderMessageDTO;
import org.insertcoin.insertcoinpaymentservice.service.CardService;
import org.insertcoin.insertcoinpaymentservice.service.PixService;
import org.springframework.stereotype.Component;

@Component
public class PaymentOrchestrator {

    private final PixService pixService;
    private final CardService cardService;

    public PaymentOrchestrator(PixService pixService, CardService cardService) {
        this.pixService = pixService;
        this.cardService = cardService;
    }

    public void process(OrderMessageDTO order) throws Exception {
        switch (order.getPaymentMethod().toUpperCase()) {
            case "PIX" -> pixService.processPix(order);
            case "CARD" -> cardService.processCard(order);
            default -> throw new RuntimeException("Payment method not supported.");
        }
    }
}
