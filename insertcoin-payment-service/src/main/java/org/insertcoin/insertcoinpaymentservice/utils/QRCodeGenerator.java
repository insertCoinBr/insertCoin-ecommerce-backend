package org.insertcoin.insertcoinpaymentservice.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.Base64;
import java.util.UUID;

@Component
public class QRCodeGenerator {

    public String generate(String orderId, BigDecimal amount) throws Exception {
        String data = "PIX://" + orderId + "/" + amount.toPlainString();

        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix matrix = writer.encode(data, BarcodeFormat.QR_CODE, 250, 250);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(matrix, "PNG", stream);

        return Base64.getEncoder().encodeToString(stream.toByteArray());
    }
}