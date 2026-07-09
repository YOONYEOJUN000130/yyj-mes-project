package com.yyj.mes_server.web.controller;

import com.yyj.mes_server.domain.analysis.service.ProductCycleTimeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProductCycleTimeController {

    private final ProductCycleTimeService productCycleTimeService;

    public ProductCycleTimeController(ProductCycleTimeService productCycleTimeService) {
        this.productCycleTimeService = productCycleTimeService;
    }

    @GetMapping("/product-cycle-times")
    public String list(Model model) {
        model.addAttribute("results", productCycleTimeService.analyze());
        return "product-cycle-times/list";
    }
}