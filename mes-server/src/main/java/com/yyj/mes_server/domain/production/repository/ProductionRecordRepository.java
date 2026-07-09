package com.yyj.mes_server.domain.production.repository;

import com.yyj.mes_server.domain.production.entity.ProductionRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductionRecordRepository extends JpaRepository<ProductionRecord, Long> {

    List<ProductionRecord> findAllByOrderByRecordedAtDesc();
}