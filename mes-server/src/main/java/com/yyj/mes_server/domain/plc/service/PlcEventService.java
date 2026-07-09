package com.yyj.mes_server.domain.plc.service;

import com.yyj.mes_server.domain.plc.entity.PlcEventLog;
import com.yyj.mes_server.domain.plc.repository.PlcEventLogRepository;
import com.yyj.mes_server.domain.product.entity.FinishedProduct;
import com.yyj.mes_server.domain.product.repository.FinishedProductRepository;
import com.yyj.mes_server.domain.production.entity.ProductionRecord;
import com.yyj.mes_server.domain.production.repository.ProductionRecordRepository;
import com.yyj.mes_server.web.api.dto.PlcEventRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PlcEventService {

    private final PlcEventLogRepository plcEventLogRepository;
    private final FinishedProductRepository finishedProductRepository;
    private final ProductionRecordRepository productionRecordRepository;

    public PlcEventService(
            PlcEventLogRepository plcEventLogRepository,
            FinishedProductRepository finishedProductRepository,
            ProductionRecordRepository productionRecordRepository
    ) {
        this.plcEventLogRepository = plcEventLogRepository;
        this.finishedProductRepository = finishedProductRepository;
        this.productionRecordRepository = productionRecordRepository;
    }

    @Transactional
    public PlcEventLog saveEvent(PlcEventRequest request) {
        if (plcEventLogRepository.existsByEventId(request.eventId())) {
            throw new IllegalArgumentException("이미 저장된 PLC 이벤트입니다: " + request.eventId());
        }

        PlcEventLog eventLog = new PlcEventLog(
                request.eventId(),
                request.lineCode(),
                request.equipmentCode(),
                request.processCode(),
                request.productSerialNo(),
                request.eventType(),
                request.productionQuantity(),
                request.cycleTimeSeconds(),
                request.result(),
                request.equipmentStatus(),
                request.occurredAt(),
                LocalDateTime.now()
        );

        PlcEventLog savedEvent = plcEventLogRepository.save(eventLog);

        saveProductionRecord(savedEvent);
        registerFinishedProductIfCompleted(savedEvent);

        return savedEvent;
    }

    private void saveProductionRecord(PlcEventLog savedEvent) {
        ProductionRecord productionRecord = new ProductionRecord(
                savedEvent.getProductSerialNo(),
                savedEvent.getProcessCode(),
                savedEvent.getProductionQuantity(),
                savedEvent.getCycleTimeSeconds(),
                savedEvent.getResult(),
                savedEvent.getOccurredAt()
        );

        productionRecordRepository.save(productionRecord);
    }

    private void registerFinishedProductIfCompleted(PlcEventLog savedEvent) {
        if (finishedProductRepository.existsByProductSerialNo(savedEvent.getProductSerialNo())) {
            return;
        }

        List<PlcEventLog> productEvents =
                plcEventLogRepository.findByProductSerialNo(savedEvent.getProductSerialNo());

        Set<String> okProcessCodes = productEvents.stream()
                .filter(event -> "OK".equals(event.getResult()))
                .map(PlcEventLog::getProcessCode)
                .collect(Collectors.toSet());

        boolean allProcessesCompleted = okProcessCodes.containsAll(Set.of("A", "B", "C"));

        if (!allProcessesCompleted) {
            return;
        }

        FinishedProduct finishedProduct = new FinishedProduct(
                savedEvent.getProductSerialNo(),
                savedEvent.getLineCode(),
                "OK",
                LocalDateTime.now()
        );

        finishedProductRepository.save(finishedProduct);
    }
}