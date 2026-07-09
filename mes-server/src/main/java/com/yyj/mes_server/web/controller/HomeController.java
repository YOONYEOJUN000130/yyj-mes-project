package com.yyj.mes_server.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("projectName", "Virtual PLC 기반 MES 생산 데이터 수집 시스템");
        return "home";
    }
}