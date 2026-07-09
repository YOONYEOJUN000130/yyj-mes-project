package com.yyj.mes_server.domain.analysis.dto;

public record ProductCycleTimeResult(
        String productSerialNo,
        long recordCount,
        double totalCycleTimeSeconds
) {
}