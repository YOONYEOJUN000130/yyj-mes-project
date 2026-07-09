package com.yyj.virtualplcsimulator.client.dto;

import java.time.LocalDateTime;

public record PlcEventRequest(
        String eventId,
        String lineCode,
        String equipmentCode,
        String processCode,
        String productSerialNo,
        String eventType,
        Integer productionQuantity,
        Double cycleTimeSeconds,
        String result,
        String equipmentStatus,
        LocalDateTime occurredAt
) {
}