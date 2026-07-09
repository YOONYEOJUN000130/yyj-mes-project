package com.yyj.mes_server.domain.plc.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "plc_event_log")
public class PlcEventLog {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "plc_event_log_seq")
    @SequenceGenerator(
            name = "plc_event_log_seq",
            sequenceName = "plc_event_log_seq",
            allocationSize = 1
    )
    private Long id;

    @Column(name = "event_id", nullable = false, unique = true, length = 50)
    private String eventId;

    @Column(name = "line_code", nullable = false, length = 30)
    private String lineCode;

    @Column(name = "equipment_code", nullable = false, length = 30)
    private String equipmentCode;

    @Column(name = "process_code", nullable = false, length = 10)
    private String processCode;

    @Column(name = "product_serial_no", nullable = false, length = 30)
    private String productSerialNo;

    @Column(name = "event_type", nullable = false, length = 30)
    private String eventType;

    @Column(name = "production_quantity", nullable = false)
    private Integer productionQuantity;

    @Column(name = "cycle_time_seconds", nullable = false)
    private Double cycleTimeSeconds;

    @Column(nullable = false, length = 10)
    private String result;

    @Column(name = "equipment_status", nullable = false, length = 20)
    private String equipmentStatus;

    @Column(name = "occurred_at", nullable = false)
    private LocalDateTime occurredAt;

    @Column(name = "received_at", nullable = false)
    private LocalDateTime receivedAt;

    protected PlcEventLog() {
    }

    public PlcEventLog(
            String eventId,
            String lineCode,
            String equipmentCode,
            String processCode,
            String productSerialNo,
            String eventType,
            Integer productionQuantity,
            Double cycleTimeSeconds,
            String result,
            String equipmentStatus,
            LocalDateTime occurredAt,
            LocalDateTime receivedAt
    ) {
        this.eventId = eventId;
        this.lineCode = lineCode;
        this.equipmentCode = equipmentCode;
        this.processCode = processCode;
        this.productSerialNo = productSerialNo;
        this.eventType = eventType;
        this.productionQuantity = productionQuantity;
        this.cycleTimeSeconds = cycleTimeSeconds;
        this.result = result;
        this.equipmentStatus = equipmentStatus;
        this.occurredAt = occurredAt;
        this.receivedAt = receivedAt;
    }

    public Long getId() {
        return id;
    }

    public String getEventId() {
        return eventId;
    }

    public String getLineCode() {
        return lineCode;
    }

    public String getEquipmentCode() {
        return equipmentCode;
    }

    public String getProcessCode() {
        return processCode;
    }

    public String getProductSerialNo() {
        return productSerialNo;
    }

    public String getEventType() {
        return eventType;
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

    public String getEquipmentStatus() {
        return equipmentStatus;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }

    public LocalDateTime getReceivedAt() {
        return receivedAt;
    }
}