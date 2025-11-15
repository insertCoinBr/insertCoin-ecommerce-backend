package org.insertcoin.insertcoinpaymentservice.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Year;
import java.util.Base64;

@Service
public class PaymentService {

    public String generatePixQrCode(String orderId, Double amount) throws WriterException, IOException {
        String qrContent = "PIX://" + orderId + "/" + amount;
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(qrContent, BarcodeFormat.QR_CODE, 250, 250);

        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        byte[] pngData = pngOutputStream.toByteArray();

        return Base64.getEncoder().encodeToString(pngData);
    }

    public void validateCard(String number, String holderName, int expiryMonth, int expiryYear, String cvv) {

        if (!number.matches("\\d{13,19}")) {
            throw new RuntimeException("Número do cartão inválido.");
        }

        if (!cvv.matches("\\d{3,4}")) {
            throw new RuntimeException("CVV inválido.");
        }

        if (expiryMonth < 1 || expiryMonth > 12) {
            throw new RuntimeException("Mês de validade inválido.");
        }

        if (expiryYear < Year.now().getValue()) {
            throw new RuntimeException("Ano de validade inválido.");
        }

        if (holderName == null || holderName.isBlank()) {
            throw new RuntimeException("Nome do titular vazio.");
        }

        if (!isValidLuhn(number)) {
            throw new RuntimeException("Número do cartão inválido (Luhn check).");
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
        return (sum % 10 == 0);
    }

}
