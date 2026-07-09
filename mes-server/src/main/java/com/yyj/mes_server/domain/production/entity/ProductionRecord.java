package com.yyj.mes_server.domain.production.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "production_record")
public class ProductionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "production_record_seq")
    @SequenceGenerator(
            name = "production_record_seq",
            sequenceName = "production_record_seq",
            allocationSize = 1
    )
    private Long id;

    @Column(name = "product_serial_no", nullable = false, length = 30)
    private String productSerialNo;

    @Column(name = "process_code", nullable = false, length = 10)
    private String processCode;

    @Column(name = "production_quantity", nullable = false)
    private Integer productionQuantity;

    @Column(name = "cycle_time_seconds", nullable = false)
    private Double cycleTimeSeconds;

    @Column(nullable = false, length = 10)
    private String result;

    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt;

    protected ProductionRecord() {
    }

    public ProductionRecord(
            String productSerialNo,
            String processCode,
            Integer productionQuantity,
            Double cycleTimeSeconds,
            String result,
            LocalDateTime recordedAt
    ) {
        this.productSerialNo = productSerialNo;
        this.processCode = processCode;
        this.productionQuantity = productionQuantity;
        this.cycleTimeSeconds = cycleTimeSeconds;
        this.result = result;
        this.recordedAt = recordedAt;
    }

    public Long getId() {
        return id;
    }

    public String getProductSerialNo() {
        return productSerialNo;
    }

    public String getProcessCode() {
        return processCode;
    }

    public Integer getProductionQuantity() {
        return productionQuantity;
    }

    public Double getCycleTimeSeconds() {
        return cycleTimeSeconds;
    }

    public String getResult() {
        return result;
    }

    public LocalDateTime getRecordedAt() {
        return recordedAt;
    }
}