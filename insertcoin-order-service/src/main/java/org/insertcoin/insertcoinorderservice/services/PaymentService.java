package org.insertcoin.insertcoinorderservice.services;

import org.insertcoin.insertcoinorderservice.entities.PaymentEntity;
import org.insertcoin.insertcoinorderservice.repositories.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    public PaymentEntity getPaymentByOrderId(UUID orderId) {
        return paymentRepository.findByOrderId(orderId)
                .orElse(null);
    }
}
