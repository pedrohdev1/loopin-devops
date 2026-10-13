package com.loopin.backend.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class ResultadoProjeto {
    private String id = UUID.randomUUID().toString();
    private LocalDate data = LocalDate.now();
    private String descricao;
    private BigDecimal investimentoAdicional = BigDecimal.ZERO;
    private BigDecimal lucroObtido = BigDecimal.ZERO;
    private BigDecimal aumentoProdutividadePercentual;
    private Integer progressoPercentual;

    public ResultadoProjeto() {}
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public BigDecimal getInvestimentoAdicional() { return investimentoAdicional; }
    public void setInvestimentoAdicional(BigDecimal investimentoAdicional) { this.investimentoAdicional = investimentoAdicional; }
    public BigDecimal getLucroObtido() { return lucroObtido; }
    public void setLucroObtido(BigDecimal lucroObtido) { this.lucroObtido = lucroObtido; }
    public BigDecimal getAumentoProdutividadePercentual() { return aumentoProdutividadePercentual; }
    public void setAumentoProdutividadePercentual(BigDecimal aumentoProdutividadePercentual) { this.aumentoProdutividadePercentual = aumentoProdutividadePercentual; }
    public Integer getProgressoPercentual() { return progressoPercentual; }
    public void setProgressoPercentual(Integer progressoPercentual) { this.progressoPercentual = progressoPercentual; }
}
