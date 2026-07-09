package com.yyj.mes_server.web.controller;

import com.yyj.mes_server.domain.admin.service.DataResetService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AdminController {

    private final DataResetService dataResetService;

    public AdminController(DataResetService dataResetService) {
        this.dataResetService = dataResetService;
    }

    @PostMapping("/reset-test-data")
    public String resetTestData() {
        dataResetService.resetTestData();
        return "redirect:/";
    }
}