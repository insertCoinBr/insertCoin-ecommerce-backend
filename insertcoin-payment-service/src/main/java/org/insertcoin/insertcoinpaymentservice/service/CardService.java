package org.insertcoin.insertcoinpaymentservice.service;

import org.insertcoin.insertcoinpaymentservice.dtos.CardPaymentCreatedDTO;
import org.insertcoin.insertcoinpaymentservice.dtos.OrderMessageDTO;
import org.springframework.stereotype.Service;

@Service
public class CardService {

    private final PaymentService paymentService;
    private final EmailService emailService;

    public CardService(PaymentService paymentService, EmailService emailService) {
        this.paymentService = paymentService;
        this.emailService = emailService;
    }

    public void processCard(OrderMessageDTO order) {

        paymentService.validateCard(order.getCard());

        CardPaymentCreatedDTO card = paymentService.createCardPayment(order);

        emailService.buildCardEmail(
                card.orderNumber(),
                card.customerEmail(),
                card.amount().doubleValue(),
                order.getProducts(),
                order.getCurrency()
        );
    }
}
