package com.yyj.mes_server.web.controller;

import com.yyj.mes_server.domain.analysis.dto.ProcessAnalysisResult;
import com.yyj.mes_server.domain.analysis.service.BottleneckAnalysisService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class AnalysisController {

    private final BottleneckAnalysisService bottleneckAnalysisService;

    public AnalysisController(BottleneckAnalysisService bottleneckAnalysisService) {
        this.bottleneckAnalysisService = bottleneckAnalysisService;
    }

    @GetMapping("/analysis")
    public String dashboard(Model model) {
        List<ProcessAnalysisResult> results = bottleneckAnalysisService.analyze();

        ProcessAnalysisResult bottleneck = results.stream()
                .filter(ProcessAnalysisResult::bottleneck)
                .findFirst()
                .orElse(null);

        model.addAttribute("results", results);
        model.addAttribute("bottleneck", bottleneck);

        return "analysis/dashboard";
    }
}