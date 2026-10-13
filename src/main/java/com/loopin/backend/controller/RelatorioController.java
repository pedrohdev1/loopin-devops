package com.loopin.backend.controller;

import com.loopin.backend.dto.DashboardResponse;
import com.loopin.backend.service.RelatorioService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/relatorios")
public class RelatorioController {
    private final RelatorioService relatorioService;
    public RelatorioController(RelatorioService relatorioService) { this.relatorioService = relatorioService; }

    @GetMapping("/dashboard")
    public DashboardResponse dashboard(@RequestParam(required = false) String estrategiaId,
                                       @RequestParam(required = false) String projetoId) {
        return relatorioService.dashboard(estrategiaId, projetoId);
    }
}
