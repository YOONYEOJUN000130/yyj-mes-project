package com.yyj.mes_server.domain.product.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "finished_product")
public class FinishedProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "finished_product_seq")
    @SequenceGenerator(
            name = "finished_product_seq",
            sequenceName = "finished_product_seq",
            allocationSize = 1
    )
    private Long id;

    @Column(name = "product_serial_no", nullable = false, unique = true, length = 30)
    private String productSerialNo;

    @Column(name = "line_code", nullable = false, length = 30)
    private String lineCode;

    @Column(name = "final_result", nullable = false, length = 10)
    private String finalResult;

    @Column(name = "completed_at", nullable = false)
    private LocalDateTime completedAt;

    protected FinishedProduct() {
    }

    public FinishedProduct(String productSerialNo, String lineCode, String finalResult, LocalDateTime completedAt) {
        this.productSerialNo = productSerialNo;
        this.lineCode = lineCode;
        this.finalResult = finalResult;
        this.completedAt = completedAt;
    }

    public Long getId() {
        return id;
    }

    public String getProductSerialNo() {
        return productSerialNo;
    }

    public String getLineCode() {
        return lineCode;
    }

    public String getFinalResult() {
        return finalResult;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }
}