package com.yyj.mes_server.domain.analysis.service;

import com.yyj.mes_server.domain.analysis.dto.ProcessAnalysisResult;
import com.yyj.mes_server.domain.production.entity.ProductionRecord;
import com.yyj.mes_server.domain.production.repository.ProductionRecordRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BottleneckAnalysisService {

    private final ProductionRecordRepository productionRecordRepository;

    public BottleneckAnalysisService(ProductionRecordRepository productionRecordRepository) {
        this.productionRecordRepository = productionRecordRepository;
    }

    public List<ProcessAnalysisResult> analyze() {
        List<ProductionRecord> records = productionRecordRepository.findAll();

        if (records.isEmpty()) {
            return List.of();
        }

        Map<String, List<ProductionRecord>> recordsByProcess = records.stream()
                .filter(record -> "OK".equals(record.getResult()))
                .collect(Collectors.groupingBy(ProductionRecord::getProcessCode));

        List<ProcessAnalysisResult> results = recordsByProcess.entrySet().stream()
                .map(entry -> toAnalysisResult(entry.getKey(), entry.getValue(), false))
                .sorted(Comparator.comparing(ProcessAnalysisResult::processCode))
                .toList();

        double lowestThroughput = results.stream()
                .mapToDouble(ProcessAnalysisResult::throughputPerHour)
                .min()
                .orElse(0.0);

        return results.stream()
                .map(result -> new ProcessAnalysisResult(
                        result.processCode(),
                        result.recordCount(),
                        result.averageCycleTimeSeconds(),
                        result.throughputPerHour(),
                        result.throughputPerHour() == lowestThroughput
                ))
                .toList();
    }

    private ProcessAnalysisResult toAnalysisResult(
            String processCode,
            List<ProductionRecord> records,
            boolean bottleneck
    ) {
        double averageCycleTime = records.stream()
                .mapToDouble(ProductionRecord::getCycleTimeSeconds)
                .average()
                .orElse(0.0);

        double throughputPerHour = averageCycleTime > 0 ? 3600 / averageCycleTime : 0.0;

        return new ProcessAnalysisResult(
                processCode,
                records.size(),
                round(averageCycleTime),
                round(throughputPerHour),
                bottleneck
        );
    }

    private double round(double value) {
        return Math.round(value * 10) / 10.0;
    }
}