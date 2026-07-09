package com.yyj.mes_server.web.controller;

import com.yyj.mes_server.domain.analysis.service.ProductProgressService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProductProgressController {

    private final ProductProgressService productProgressService;

    public ProductProgressController(ProductProgressService productProgressService) {
        this.productProgressService = productProgressService;
    }

    @GetMapping("/product-progress")
    public String productProgress(Model model) {
        model.addAttribute("progressList", productProgressService.getProductProgressList());
        return "product-progress/list";
    }
}