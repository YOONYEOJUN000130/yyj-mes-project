package com.yyj.virtualplcsimulator.client;

import com.yyj.virtualplcsimulator.client.dto.PlcEventRequest;
import com.yyj.virtualplcsimulator.client.dto.PlcEventResponse;
import com.yyj.virtualplcsimulator.security.PlcSignatureService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class MesServerClient {

    private final RestClient restClient;
    private final String apiKey;
    private final PlcSignatureService plcSignatureService;

    public MesServerClient(
            RestClient.Builder restClientBuilder,
            @Value("${mes-server.base-url}") String mesServerBaseUrl,
            @Value("${mes-server.api-key}") String apiKey,
            PlcSignatureService plcSignatureService
    ) {
        this.restClient = restClientBuilder
                .baseUrl(mesServerBaseUrl)
                .build();
        this.apiKey = apiKey;
        this.plcSignatureService = plcSignatureService;
    }

    public PlcEventResponse sendPlcEvent(PlcEventRequest request) {
        String timestamp = plcSignatureService.createTimestamp();
        String signature = plcSignatureService.createSignature(request, timestamp);

        return restClient.post()
                .uri("/api/plc/events")
                .header("X-PLC-API-KEY", apiKey)
                .header("X-PLC-TIMESTAMP", timestamp)
                .header("X-PLC-SIGNATURE", signature)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(PlcEventResponse.class);
    }
}