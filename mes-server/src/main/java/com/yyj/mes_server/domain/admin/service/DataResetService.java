package com.yyj.mes_server.domain.admin.service;

import com.yyj.mes_server.domain.plc.repository.PlcEventLogRepository;
import com.yyj.mes_server.domain.product.repository.FinishedProductRepository;
import com.yyj.mes_server.domain.production.repository.ProductionRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DataResetService {

    private final PlcEventLogRepository plcEventLogRepository;
    private final ProductionRecordRepository productionRecordRepository;
    private final FinishedProductRepository finishedProductRepository;

    public DataResetService(
            PlcEventLogRepository plcEventLogRepository,
            ProductionRecordRepository productionRecordRepository,
            FinishedProductRepository finishedProductRepository
    ) {
        this.plcEventLogRepository = plcEventLogRepository;
        this.productionRecordRepository = productionRecordRepository;
        this.finishedProductRepository = finishedProductRepository;
    }

    @Transactional
    public void resetTestData() {
        finishedProductRepository.deleteAll();
        productionRecordRepository.deleteAll();
        plcEventLogRepository.deleteAll();
    }
}