package com.yyj.mes_server.web.controller;

import com.yyj.mes_server.domain.product.repository.FinishedProductRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FinishedProductController {

    private final FinishedProductRepository finishedProductRepository;

    public FinishedProductController(FinishedProductRepository finishedProductRepository) {
        this.finishedProductRepository = finishedProductRepository;
    }

    @GetMapping("/finished-products")
    public String list(Model model) {
        model.addAttribute("products", finishedProductRepository.findAllByOrderByCompletedAtDesc());
        return "finished-products/list";
    }
}