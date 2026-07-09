package com.yyj.mes_server.global.security;

import com.yyj.mes_server.web.api.dto.PlcEventRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.HexFormat;

@Service
public class PlcRequestAuthenticationService {

    private static final String HMAC_ALGORITHM = "HmacSHA256";

    private final String apiKey;
    private final String hmacSecretKey;
    private final long timestampValidSeconds;

    public PlcRequestAuthenticationService(
            @Value("${plc.api-key}") String apiKey,
            @Value("${plc.hmac-secret-key}") String hmacSecretKey,
            @Value("${plc.timestamp-valid-seconds}") long timestampValidSeconds
    ) {
        this.apiKey = apiKey;
        this.hmacSecretKey = hmacSecretKey;
        this.timestampValidSeconds = timestampValidSeconds;
    }

    public boolean isValid(
            String requestApiKey,
            String requestTimestamp,
            String requestSignature,
            PlcEventRequest request
    ) {
        if (!apiKey.equals(requestApiKey)) {
            return false;
        }

        if (isBlank(requestTimestamp) || isBlank(requestSignature)) {
            return false;
        }

        if (!isTimestampValid(requestTimestamp)) {
            return false;
        }

        String expectedSignature = createSignature(request, requestTimestamp);

        return MessageDigest.isEqual(
                expectedSignature.getBytes(StandardCharsets.UTF_8),
                requestSignature.getBytes(StandardCharsets.UTF_8)
        );
    }

    private boolean isTimestampValid(String requestTimestamp) {
        try {
            LocalDateTime timestamp = LocalDateTime.parse(requestTimestamp);
            long differenceSeconds = Math.abs(Duration.between(timestamp, LocalDateTime.now()).getSeconds());

            return differenceSeconds <= timestampValidSeconds;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private String createSignature(PlcEventRequest request, String requestTimestamp) {
        String message = createSignatureMessage(request, requestTimestamp);

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

    private String createSignatureMessage(PlcEventRequest request, String requestTimestamp) {
        return request.eventId()
                + ":"
                + request.productSerialNo()
                + ":"
                + request.processCode()
                + ":"
                + request.result()
                + ":"
                + requestTimestamp;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}