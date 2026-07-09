package com.yyj.virtualplcsimulator.security;

import com.yyj.virtualplcsimulator.client.dto.PlcEventRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HexFormat;

@Service
public class PlcSignatureService {

    private static final String HMAC_ALGORITHM = "HmacSHA256";

    private final String hmacSecretKey;

    public PlcSignatureService(@Value("${mes-server.hmac-secret-key}") String hmacSecretKey) {
        this.hmacSecretKey = hmacSecretKey;
    }

    public String createTimestamp() {
        return LocalDateTime.now().toString();
    }

    public String createSignature(PlcEventRequest request, String timestamp) {
        String message = createSignatureMessage(request, timestamp);

        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            SecretKeySpec secretKeySpec = new SecretKeySpec(
                    hmacSecretKey.getBytes(StandardCharsets.UTF_8),
                    HMAC_ALGORITHM
            );

            mac.init(secretKeySpec);

            byte[] signatureBytes = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(signatureBytes);
        } catch (Exception e) {
            throw new IllegalStateException("PLC 요청 서명 생성 중 오류가 발생했습니다.", e);
        }
    }

    private String createSignatureMessage(PlcEventRequest request, String timestamp) {
        return request.eventId()
                + ":"
                + request.productSerialNo()
                + ":"
                + request.processCode()
                + ":"
                + request.result()
                + ":"
                + timestamp;
    }
}