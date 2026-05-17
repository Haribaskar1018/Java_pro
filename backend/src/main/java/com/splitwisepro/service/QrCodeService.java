package com.splitwisepro.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Base64;
import java.util.EnumMap;
import java.util.Map;

@Service
public class QrCodeService {

    /**
     * Generates a UPI deep-link QR code.
     * UPI URI format: upi://pay?pa=UPI_ID&pn=NAME&am=AMOUNT&tn=NOTE&cu=INR
     */
    public String generateUpiQrBase64(String upiId, String payeeName,
                                       BigDecimal amount, String note) throws WriterException, IOException {
        String upiUri = buildUpiUri(upiId, payeeName, amount, note);
        return generateQrBase64(upiUri, 300, 300);
    }

    public String buildUpiUri(String upiId, String payeeName, BigDecimal amount, String note) {
        StringBuilder sb = new StringBuilder("upi://pay?");
        sb.append("pa=").append(upiId);
        sb.append("&pn=").append(encode(payeeName));
        if (amount != null && amount.compareTo(BigDecimal.ZERO) > 0) {
            sb.append("&am=").append(amount.toPlainString());
        }
        if (note != null && !note.isBlank()) {
            sb.append("&tn=").append(encode(note));
        }
        sb.append("&cu=INR");
        return sb.toString();
    }

    private String encode(String value) {
        return value.replace(" ", "%20");
    }

    public String generateQrBase64(String content, int width, int height)
            throws WriterException, IOException {
        Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.MARGIN, 2);

        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix matrix = writer.encode(content, BarcodeFormat.QR_CODE, width, height, hints);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(matrix, "PNG", baos);

        return "data:image/png;base64," + Base64.getEncoder().encodeToString(baos.toByteArray());
    }
}
