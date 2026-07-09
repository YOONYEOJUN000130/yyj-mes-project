package com.yyj.mes_server.domain.analysis.dto;

public record ProcessAnalysisResult(
        String processCode,
        long recordCount,
        double averageCycleTimeSeconds,
        double throughputPerHour,
        boolean bottleneck
) {
}