package org.insertcoin.insertcoinpaymentservice.service;

import jakarta.transaction.Transactional;
import org.insertcoin.insertcoinpaymentservice.clients.OrderClient;
import org.insertcoin.insertcoinpaymentservice.dtos.*;
import org.insertcoin.insertcoinpaymentservice.entity.PaymentEntity;
import org.insertcoin.insertcoinpaymentservice.publisher.EmailPublisher;
import org.insertcoin.insertcoinpaymentservice.publisher.PaymentStatusPublisher;
import org.insertcoin.insertcoinpaymentservice.repository.PaymentRepository;
import org.insertcoin.insertcoinpaymentservice.utils.QRCodeGenerator;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository repo;
    private final QRCodeGenerator qrCodeGenerator;
    private final PaymentStatusPublisher paymentStatusPublisher;
    private final EmailPublisher emailPublisher;
    private final PaymentRepository paymentRepository;
    private final OrderClient orderClient;

    public PaymentService(PaymentRepository repo, QRCodeGenerator qrCodeGenerator, PaymentStatusPublisher paymentStatusPublisher, EmailPublisher emailPublisher, PaymentRepository paymentRepository, OrderClient orderClient) {
        this.repo = repo;
        this.qrCodeGenerator = qrCodeGenerator;
        this.paymentStatusPublisher = paymentStatusPublisher;
        this.emailPublisher = emailPublisher;
        this.paymentRepository = paymentRepository;
        this.orderClient = orderClient;
    }

    @Transactional
    public PixPaymentCreatedDTO createPixPayment(OrderMessageDTO order) throws Exception {

        String qrCode = qrCodeGenerator.generate(order.getOrderNumber(), order.getAmount());

        PaymentEntity payment = PaymentEntity.createPix(
                order.getOrderId(),
                order.getAmount(),
                qrCode
        );

        repo.save(payment);

        PaymentStatusDTO statusDTO = new PaymentStatusDTO();
        statusDTO.setOrderId(order.getOrderId());
        statusDTO.setStatus("WAITING_PIX_PAYMENT");
        paymentStatusPublisher.publish(statusDTO);

        return new PixPaymentCreatedDTO(
                order.getOrderId(),
                order.getOrderNumber(),
                order.getCustomerEmail(),
                qrCode,
                order.getAmount()
        );
    }

    public void validateCard(CardDTO card) {
        if (card == null) {
            throw new RuntimeException("Cartão não fornecido.");
        }

        validateCard(
                card.getNumber(),
                card.getHolderName(),
                card.getExpiryMonth(),
                card.getExpiryYear(),
                card.getCvv()
        );
    }

    private void validateCard(String number, String holderName, int expiryMonth, int expiryYear, String cvv) {

        if (!number.matches("\\d{13,19}")) {
            throw new RuntimeException("Número do cartão inválido.");
        }

        if (!cvv.matches("\\d{3,4}")) {
            throw new RuntimeException("CVV inválido.");
        }

        if (expiryMonth < 1 || expiryMonth > 12) {
            throw new RuntimeException("Mês inválido.");
        }

        if (expiryYear < Year.now().getValue()) {
            throw new RuntimeException("Ano inválido.");
        }

        if (!isValidLuhn(number)) {
            throw new RuntimeException("Cartão inválido.");
        }
    }

    private boolean isValidLuhn(String number) {
        int sum = 0;
        boolean alternate = false;

        for (int i = number.length() - 1; i >= 0; i--) {
            int n = Integer.parseInt(number.substring(i, i + 1));
            if (alternate) {
                n *= 2;
                if (n > 9) n -= 9;
            }
            sum += n;
            alternate = !alternate;
        }
        return sum % 10 == 0;
    }

    @Transactional
    public CardPaymentCreatedDTO createCardPayment(OrderMessageDTO order) {

        String transactionId = "TRX-" + UUID.randomUUID();

        PaymentEntity payment = PaymentEntity.createCard(
                order.getOrderId(),
                order.getAmount(),
                transactionId
        );

        repo.save(payment);

        PaymentStatusDTO statusDTO = new PaymentStatusDTO();
        statusDTO.setOrderId(order.getOrderId());
        statusDTO.setStatus("PAID");
        paymentStatusPublisher.publish(statusDTO);

        return new CardPaymentCreatedDTO(
                order.getOrderId(),
                order.getOrderNumber(),
                order.getCustomerEmail(),
                transactionId,
                order.getAmount()
        );
    }

    @Transactional
    public PixPaymentConfirmedDTO confirmPixPayment(String pixKey) {

        PaymentEntity payment = paymentRepository.findByPixKey(pixKey)
                .orElseThrow(() -> new RuntimeException("PIX not found."));

        if (!payment.getStatus().equals("WAITING_PIX_PAYMENT")) {
            throw new RuntimeException("Payment already processed.");
        }

        payment.setStatus("PAID");
        payment.setPaidAt(LocalDateTime.now());
        paymentRepository.save(payment);

        PaymentStatusDTO statusDTO = new PaymentStatusDTO();
        statusDTO.setOrderId(payment.getOrderId());
        statusDTO.setStatus("PAID");
        paymentStatusPublisher.publish(statusDTO);

        OrderNotificationDataDTO orderData =
                orderClient.getNotificationData(payment.getOrderId());

        if (orderData.getCustomerEmail() == null || orderData.getCustomerEmail().isBlank()) {
            throw new RuntimeException("Order is missing customerEmail.");
        }

        return new PixPaymentConfirmedDTO(
                orderData.getOrderId(),
                orderData.getOrderNumber(),
                orderData.getCustomerEmail(),
                orderData.getAmount(),
                orderData.getCurrency(),
                orderData.getProducts()
        );
    }

}
