package com.loopin.backend.controller;

import com.loopin.backend.dto.AiPontuacaoResponse;
import com.loopin.backend.service.AiService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ia")
public class AiController {
    private final AiService aiService;
    public AiController(AiService aiService) { this.aiService = aiService; }

    @PostMapping("/ideias/{id}/pontuar")
    public AiPontuacaoResponse pontuarIdeia(@PathVariable String id) {
        return aiService.pontuarIdeia(id);
    }
}
