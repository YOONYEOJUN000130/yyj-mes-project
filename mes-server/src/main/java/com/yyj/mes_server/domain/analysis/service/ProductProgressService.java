package com.yyj.mes_server.domain.analysis.service;

import com.yyj.mes_server.domain.analysis.dto.ProductProgressResult;
import com.yyj.mes_server.domain.production.entity.ProductionRecord;
import com.yyj.mes_server.domain.production.repository.ProductionRecordRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductProgressService {

    private static final String NOT_RECEIVED = "미수신";

    private final ProductionRecordRepository productionRecordRepository;

    public ProductProgressService(ProductionRecordRepository productionRecordRepository) {
        this.productionRecordRepository = productionRecordRepository;
    }

    public List<ProductProgressResult> getProductProgressList() {
        List<ProductionRecord> records = productionRecordRepository.findAll();

        Map<String, List<ProductionRecord>> recordsByProduct = records.stream()
                .collect(Collectors.groupingBy(ProductionRecord::getProductSerialNo));

        return recordsByProduct.entrySet().stream()
                .map(entry -> createProductProgress(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(ProductProgressResult::productSerialNo))
                .toList();
    }

    private ProductProgressResult createProductProgress(
            String productSerialNo,
            List<ProductionRecord> records
    ) {
        String processAResult = findProcessResult(records, "A");
        String processBResult = findProcessResult(records, "B");
        String processCResult = findProcessResult(records, "C");

        String progressStatus = decideProgressStatus(
                processAResult,
                processBResult,
                processCResult
        );

        return new ProductProgressResult(
                productSerialNo,
                processAResult,
                processBResult,
                processCResult,
                progressStatus
        );
    }

    private String findProcessResult(List<ProductionRecord> records, String processCode) {
        return records.stream()
                .filter(record -> processCode.equals(record.getProcessCode()))
                .map(ProductionRecord::getResult)
                .findFirst()
                .orElse(NOT_RECEIVED);
    }

    private String decideProgressStatus(
            String processAResult,
            String processBResult,
            String processCResult
    ) {
        if (isNotReceived(processAResult) || isNotReceived(processBResult) || isNotReceived(processCResult)) {
            return "INCOMPLETE";
        }

        if ("NG".equals(processAResult) || "NG".equals(processBResult) || "NG".equals(processCResult)) {
            return "DEFECT";
        }

        return "COMPLETED";
    }

    private boolean isNotReceived(String processResult) {
        return NOT_RECEIVED.equals(processResult);
    }
}