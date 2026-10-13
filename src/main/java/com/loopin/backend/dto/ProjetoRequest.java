package com.loopin.backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ProjetoRequest(
        @NotBlank(message = "O nome é obrigatório") @Size(max = 160) String nome,
        @NotBlank(message = "A descrição é obrigatória") @Size(max = 5000) String descricao,
        @NotBlank(message = "A etapa é obrigatória") String etapa,
        @NotBlank(message = "O status é obrigatório") String status,
        @NotNull(message = "O investimento inicial é obrigatório") @DecimalMin(value = "0.0", message = "O investimento não pode ser negativo") BigDecimal investimento,
        @NotNull(message = "O prazo é obrigatório") LocalDate prazo,
        @DecimalMin(value = "0.0", message = "A produtividade não pode ser negativa") BigDecimal aumentoProdutividadePercentual,
        @Min(0) @Max(100) Integer progressoPercentual,
        @NotBlank(message = "A estratégia vigente é obrigatória") String estrategiaId,
        String ideiaId
) {}
