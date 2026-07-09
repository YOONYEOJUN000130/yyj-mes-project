package com.yyj.virtualplcsimulator.client.dto;

public record PlcEventResponse(
        String eventId,
        String message
) {
}