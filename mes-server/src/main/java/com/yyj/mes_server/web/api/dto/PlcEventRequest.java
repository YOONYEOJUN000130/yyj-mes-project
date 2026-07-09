package com.yyj.mes_server.web.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public record PlcEventRequest(
        @NotBlank String eventId,
        @NotBlank String lineCode,
        @NotBlank String equipmentCode,
        @NotBlank String processCode,
        @NotBlank String productSerialNo,
        @NotBlank String eventType,
        @NotNull @Positive Integer productionQuantity,
        @NotNull @Positive Double cycleTimeSeconds,
        @NotBlank String result,
        @NotBlank String equipmentStatus,
        @NotNull LocalDateTime occurredAt
) {
}