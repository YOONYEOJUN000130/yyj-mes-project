package com.yyj.mes_server.domain.analysis.dto;

public record ProductProgressResult(
        String productSerialNo,
        String processAResult,
        String processBResult,
        String processCResult,
        String progressStatus
) {
}