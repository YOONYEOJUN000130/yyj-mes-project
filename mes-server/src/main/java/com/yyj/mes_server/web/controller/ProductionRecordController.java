package com.yyj.mes_server.web.controller;

import com.yyj.mes_server.domain.production.repository.ProductionRecordRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProductionRecordController {

    private final ProductionRecordRepository productionRecordRepository;

    public ProductionRecordController(ProductionRecordRepository productionRecordRepository) {
        this.productionRecordRepository = productionRecordRepository;
    }

    @GetMapping("/production-records")
    public String list(Model model) {
        model.addAttribute("records", productionRecordRepository.findAllByOrderByRecordedAtDesc());
        return "production-records/list";
    }
}