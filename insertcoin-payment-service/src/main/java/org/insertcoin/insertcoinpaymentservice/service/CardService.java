package org.insertcoin.insertcoinpaymentservice.service;

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

        // createCardPayment()
        // paymentService.createCardPayment(order);

        emailService.buildCardEmail(
                order.getOrderNumber(),
                order.getCustomerEmail(),
                order.getAmount().doubleValue(),
                order.getProducts(),
                order.getCurrency()
        );
    }
}
