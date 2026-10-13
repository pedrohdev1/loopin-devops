package com.loopin.backend.dto;

import jakarta.validation.constraints.NotBlank;

public record AvaliacaoIdeiaRequest(
        @NotBlank(message = "O status é obrigatório") String status,
        @NotBlank(message = "A prioridade é obrigatória") String prioridade,
        String comentarioGestor
) {}
