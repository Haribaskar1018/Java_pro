package com.splitwisepro.controller;

import com.google.zxing.WriterException;
import com.splitwisepro.service.QrCodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/qr")
@RequiredArgsConstructor
@Tag(name = "QR Code", description = "UPI QR code generation APIs")
@SecurityRequirement(name = "bearerAuth")
public class QrController {

    private final QrCodeService qrCodeService;

    @GetMapping("/upi")
    @Operation(summary = "Generate UPI payment QR code")
    public ResponseEntity<Map<String, String>> generateUpiQr(
            @RequestParam String upiId,
            @RequestParam String name,
            @RequestParam(required = false, defaultValue = "0") BigDecimal amount,
            @RequestParam(required = false, defaultValue = "") String note
    ) throws WriterException, IOException {

        String qrBase64 = qrCodeService.generateUpiQrBase64(upiId, name, amount, note);
        String upiUri = qrCodeService.buildUpiUri(upiId, name, amount, note);

        return ResponseEntity.ok(Map.of(
                "qrImage", qrBase64,
                "upiUri", upiUri,
                "upiId", upiId,
                "amount", amount.toPlainString()
        ));
    }
}
