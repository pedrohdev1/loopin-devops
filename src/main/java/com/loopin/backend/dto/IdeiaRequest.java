package com.loopin.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record IdeiaRequest(
        @NotBlank(message = "O título é obrigatório") @Size(max = 140, message = "O título deve ter no máximo 140 caracteres") String titulo,
        @NotBlank(message = "A descrição é obrigatória") @Size(max = 5000, message = "A descrição deve ter no máximo 5000 caracteres") String descricao
) {}
