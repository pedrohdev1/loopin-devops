package com.loopin.backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ResultadoProjetoRequest(
        @NotBlank(message = "A descrição do resultado é obrigatória") String descricao,
        LocalDate data,
        @DecimalMin(value = "0.0", message = "O investimento adicional não pode ser negativo") BigDecimal investimentoAdicional,
        BigDecimal lucroObtido,
        @DecimalMin(value = "0.0", message = "A produtividade não pode ser negativa") BigDecimal aumentoProdutividadePercentual,
        @Min(0) @Max(100) Integer progressoPercentual
) {}
