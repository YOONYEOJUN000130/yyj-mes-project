package com.yyj.mes_server.domain.process.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "production_process")
public class ProductionProcess {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "production_process_seq")
    @SequenceGenerator(
            name = "production_process_seq",
            sequenceName = "production_process_seq",
            allocationSize = 1
    )
    private Long id;

    @Column(nullable = false, unique = true, length = 10)
    private String code;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 200)
    private String description;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    protected ProductionProcess() {
    }

    public ProductionProcess(String code, String name, String description, Integer displayOrder) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.displayOrder = displayOrder;
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }
}