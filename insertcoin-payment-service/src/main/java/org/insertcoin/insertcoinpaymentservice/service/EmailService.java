package org.insertcoin.insertcoinpaymentservice.service;

import org.insertcoin.insertcoinpaymentservice.dtos.*;
import org.insertcoin.insertcoinpaymentservice.publisher.EmailPublisher;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class EmailService {

    private final EmailPublisher publisher;

    public EmailService(EmailPublisher publisher) {
        this.publisher = publisher;
    }

    public void buildPixEmail(PixPaymentCreatedDTO dto) {

        EmailAttachmentDTO qrCodeAttachment = new EmailAttachmentDTO(
                "qrcode",
                "image/png",
                dto.getQrCode(),
                true
        );

        EmailMessageDTO email = new EmailMessageDTO();
        email.setType("PIX_PAYMENT");
        email.setTo(dto.getCustomerEmail());
        email.setSubject("Pagamento PIX - Pedido " + dto.getOrderNumber());
        email.setTemplate("pix-payment");

        email.setVariables(Map.of(
                "orderId", dto.getOrderNumber(),
                "amount", dto.getAmount()
        ));

        email.setAttachments(List.of(qrCodeAttachment));

        publisher.publish(email);
    }

    public void buildCardEmail(String orderId, String customerEmail, Double amount, List<ProductDTO> products, String currency) {
        EmailMessageDTO dto = new EmailMessageDTO();
        dto.setType("CARD_PAYMENT");
        dto.setTo(customerEmail);
        dto.setSubject("Pagamento aprovado - Pedido " + orderId);
        dto.setTemplate("card-payment-approved");

        Map<String, Object> variables = new HashMap<>();
        variables.put("orderId", orderId);
        variables.put("amount", amount);
        variables.put("products", products);
        variables.put("currency", currency);
        dto.setVariables(variables);

        dto.setAttachments(Collections.emptyList());
        publisher.publish(dto);
    }

    public void buildPixPaidEmail(PixPaymentConfirmedDTO dto) {

        EmailMessageDTO email = new EmailMessageDTO();
        email.setType("PIX_PAYMENT_CONFIRMED");
        email.setTo(dto.getCustomerEmail());
        email.setSubject("Pagamento PIX confirmado - Pedido " + dto.getOrderNumber());
        email.setTemplate("pix-payment-confirmed");

        Map<String, Object> variables = new HashMap<>();
        variables.put("orderId", dto.getOrderNumber());
        variables.put("amount", dto.getAmount());
        variables.put("currency", dto.getCurrency());
        variables.put("products", dto.getProducts());

        email.setVariables(variables);
        email.setAttachments(Collections.emptyList());
        publisher.publish(email);
    }
}
