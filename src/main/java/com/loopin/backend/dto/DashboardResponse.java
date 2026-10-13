package com.loopin.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record DashboardResponse(
        long totalProjetos,
        long projetosEmAndamento,
        long projetosConcluidos,
        BigDecimal investimentoTotal,
        BigDecimal lucroObtidoTotal,
        BigDecimal roiPercentual,
        long prazoMedioRestanteDias,
        BigDecimal aumentoProdutividadeMedioPercentual,
        BigDecimal progressoMedioPercentual,
        List<ResumoEstrategia> porEstrategia,
        List<ResumoProjeto> porProjeto
) {
    public record ResumoEstrategia(String estrategiaId, String categoria, String campanha,
                                  long quantidadeProjetos, BigDecimal investimentoTotal,
                                  BigDecimal lucroObtidoTotal, BigDecimal roiPercentual) {}
    public record ResumoProjeto(String projetoId, String nome, String status, String etapa,
                                BigDecimal investimento, BigDecimal lucroObtido,
                                BigDecimal roiPercentual, LocalDate prazo, Integer progressoPercentual) {}
}
