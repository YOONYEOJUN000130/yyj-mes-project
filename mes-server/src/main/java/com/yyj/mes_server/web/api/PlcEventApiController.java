package com.yyj.mes_server.web.api;

import com.yyj.mes_server.domain.plc.entity.PlcEventLog;
import com.yyj.mes_server.domain.plc.service.PlcEventService;
import com.yyj.mes_server.global.security.PlcRequestAuthenticationService;
import com.yyj.mes_server.web.api.dto.PlcEventRequest;
import com.yyj.mes_server.web.api.dto.PlcEventResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/plc/events")
public class PlcEventApiController {

    private final PlcEventService plcEventService;
    private final PlcRequestAuthenticationService plcRequestAuthenticationService;

    public PlcEventApiController(
            PlcEventService plcEventService,
            PlcRequestAuthenticationService plcRequestAuthenticationService
    ) {
        this.plcEventService = plcEventService;
        this.plcRequestAuthenticationService = plcRequestAuthenticationService;
    }

    @PostMapping
    public ResponseEntity<PlcEventResponse> receivePlcEvent(
            @RequestHeader(value = "X-PLC-API-KEY", required = false) String requestApiKey,
            @RequestHeader(value = "X-PLC-TIMESTAMP", required = false) String requestTimestamp,
            @RequestHeader(value = "X-PLC-SIGNATURE", required = false) String requestSignature,
            @Valid @RequestBody PlcEventRequest request
    ) {
        boolean authenticated = plcRequestAuthenticationService.isValid(
                requestApiKey,
                requestTimestamp,
                requestSignature,
                request
        );

        if (!authenticated) {
            PlcEventResponse response = new PlcEventResponse(
                    null,
                    "인증되지 않은 PLC 요청입니다."
            );

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        PlcEventLog savedEvent = plcEventService.saveEvent(request);

        PlcEventResponse response = new PlcEventResponse(
                savedEvent.getEventId(),
                "PLC 이벤트가 저장되었습니다."
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}