package com.yyj.mes_server.web.api.dto;

public record PlcEventResponse(
        String eventId,
        String message
) {
}