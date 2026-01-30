package com.medicalquiz.Controller;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/qr")
@CrossOrigin(origins = "*")
public class QRCodeController {

    @Value("${server.port:8080}")
    private String serverPort;

    /**
     * Generate QR code containing the quiz URL
     */
    @GetMapping("/generate")
    public ResponseEntity<Map<String, String>> generateQRCode(HttpServletRequest request) {
        try {
            // Build the quiz URL - points to /quiz route
            String baseUrl = getBaseUrl(request);
            String quizUrl = baseUrl + "/quiz";

            // Generate QR code
            String base64Image = generateQRCodeImage(quizUrl, 300, 300);

            Map<String, String> response = new HashMap<>();
            response.put("success", "true");
            response.put("qrImage", base64Image);
            response.put("quizUrl", quizUrl);
            response.put("message", "QR Code generated successfully");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("success", "false");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get QR code as PNG image (for direct download)
     */
    @GetMapping(value = "/image", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getQRCodeImage(HttpServletRequest request) {
        try {
            String baseUrl = getBaseUrl(request);
            String quizUrl = baseUrl + "/quiz";

            byte[] qrCode = generateQRCodeBytes(quizUrl, 400, 400);

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(qrCode);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get quiz statistics (for QR code page)
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getQuizStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalQuestions", "500+");
        stats.put("categories", "15+");
        stats.put("difficulty", "Easy to Hard");
        stats.put("quizType", "Medical Diagnosis");

        return ResponseEntity.ok(stats);
    }

    /**
     * Generate QR code and return as Base64 string
     */
    private String generateQRCodeImage(String text, int width, int height) 
            throws WriterException, IOException {
        byte[] qrCode = generateQRCodeBytes(text, width, height);
        return Base64.getEncoder().encodeToString(qrCode);
    }

    /**
     * Generate QR code as byte array
     */
    private byte[] generateQRCodeBytes(String text, int width, int height) 
            throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
        return outputStream.toByteArray();
    }

    /**
     * Get base URL from request
     */
    private String getBaseUrl(HttpServletRequest request) {
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int port = request.getServerPort();
        String contextPath = request.getContextPath();

        String baseUrl = scheme + "://" + serverName;
        
        // Only add port if it's not the default for the scheme
        if ((scheme.equals("http") && port != 80) || (scheme.equals("https") && port != 443)) {
            baseUrl += ":" + port;
        }
        
        baseUrl += contextPath;
        
        return baseUrl;
    }
}