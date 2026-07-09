package com.yyj.mes_server.web.controller;

import com.yyj.mes_server.domain.plc.repository.PlcEventLogRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PlcEventController {

    private final PlcEventLogRepository plcEventLogRepository;

    public PlcEventController(PlcEventLogRepository plcEventLogRepository) {
        this.plcEventLogRepository = plcEventLogRepository;
    }

    @GetMapping("/plc-events")
    public String list(Model model) {
        model.addAttribute("events", plcEventLogRepository.findAllByOrderByReceivedAtDesc());
        return "plc-events/list";
    }
}