package com.yyj.mes_server.domain.analysis.service;

import com.yyj.mes_server.domain.analysis.dto.ProductCycleTimeResult;
import com.yyj.mes_server.domain.production.entity.ProductionRecord;
import com.yyj.mes_server.domain.production.repository.ProductionRecordRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductCycleTimeService {

    private final ProductionRecordRepository productionRecordRepository;

    public ProductCycleTimeService(ProductionRecordRepository productionRecordRepository) {
        this.productionRecordRepository = productionRecordRepository;
    }

    public List<ProductCycleTimeResult> analyze() {
        List<ProductionRecord> records = productionRecordRepository.findAll();

        Map<String, List<ProductionRecord>> recordsByProduct = records.stream()
                .filter(record -> "OK".equals(record.getResult()))
                .collect(Collectors.groupingBy(ProductionRecord::getProductSerialNo));

        return recordsByProduct.entrySet().stream()
                .map(entry -> toResult(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(ProductCycleTimeResult::productSerialNo).reversed())
                .toList();
    }

    private ProductCycleTimeResult toResult(String productSerialNo, List<ProductionRecord> records) {
        double totalCycleTime = records.stream()
                .mapToDouble(ProductionRecord::getCycleTimeSeconds)
                .sum();

        return new ProductCycleTimeResult(
                productSerialNo,
                records.size(),
                round(totalCycleTime)
        );
    }

    private double round(double value) {
        return Math.round(value * 10) / 10.0;
    }
}