package org.insertcoin.insertcoinpaymentservice.service;

import org.insertcoin.insertcoinpaymentservice.dtos.OrderMessageDTO;
import org.insertcoin.insertcoinpaymentservice.dtos.PixPaymentCreatedDTO;
import org.springframework.stereotype.Service;

@Service
public class PixService {

    private final PaymentService paymentService;
    private final EmailService emailService;

    public PixService(PaymentService paymentService, EmailService emailService) {
        this.paymentService = paymentService;
        this.emailService = emailService;
    }

    public void processPix(OrderMessageDTO order) throws Exception {
        PixPaymentCreatedDTO pix = paymentService.createPixPayment(order);
        emailService.buildPixEmail(pix);
    }
}
