package org.insertcoin.insertcoinpaymentservice.service;

import org.insertcoin.insertcoinpaymentservice.dtos.EmailAttachmentDTO;
import org.insertcoin.insertcoinpaymentservice.dtos.EmailMessageDTO;
import org.insertcoin.insertcoinpaymentservice.dtos.ProductDTO;
import org.insertcoin.insertcoinpaymentservice.publisher.EmailPublisher;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class EmailService {

    private final EmailPublisher publisher;

    public EmailService(EmailPublisher publisher) {
        this.publisher = publisher;
    }

    public void buildPixEmail(String orderId, String customerEmail, String qrCodeBase64, Double amount) {
        EmailAttachmentDTO qrCodeAttachment = new EmailAttachmentDTO(
                "qrcode",
                "image/png",
                qrCodeBase64,
                true
        );

        EmailMessageDTO dto = new EmailMessageDTO();
        dto.setType("PIX_PAYMENT");
        dto.setTo(customerEmail);
        dto.setSubject("Pagamento PIX - Pedido " + orderId);
        dto.setTemplate("pix-payment");
        dto.setVariables(Map.of("orderId", orderId, "amount", amount));
        dto.setAttachments(List.of(qrCodeAttachment));
        publisher.publish(dto);
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

}
