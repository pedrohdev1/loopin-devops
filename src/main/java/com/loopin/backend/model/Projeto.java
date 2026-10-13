package com.loopin.backend.model;

import jakarta.validation.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "projetos")
public class Projeto {
    @Id
    private String id;
    @NotBlank(message = "O nome do projeto é obrigatório")
    private String nome;
    @NotBlank(message = "A descrição do projeto é obrigatória")
    private String descricao;
    private String etapa = "PLANEJAMENTO";
    private String status = "PLANEJADO";
    private BigDecimal investimento = BigDecimal.ZERO;
    private BigDecimal lucroObtido = BigDecimal.ZERO;
    private LocalDate prazo;
    private BigDecimal aumentoProdutividadePercentual = BigDecimal.ZERO;
    private Integer progressoPercentual = 0;
    private String estrategiaId;
    private String ideiaId;
    private String gestorResponsavelId;
    private LocalDateTime criadoEm = LocalDateTime.now();
    private LocalDateTime atualizadoEm = LocalDateTime.now();
    private List<ResultadoProjeto> resultados = new ArrayList<>();

    public Projeto() {}
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getEtapa() { return etapa; }
    public void setEtapa(String etapa) { this.etapa = etapa; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public BigDecimal getInvestimento() { return investimento; }
    public void setInvestimento(BigDecimal investimento) { this.investimento = investimento; }
    public BigDecimal getLucroObtido() { return lucroObtido; }
    public void setLucroObtido(BigDecimal lucroObtido) { this.lucroObtido = lucroObtido; }
    public LocalDate getPrazo() { return prazo; }
    public void setPrazo(LocalDate prazo) { this.prazo = prazo; }
    public BigDecimal getAumentoProdutividadePercentual() { return aumentoProdutividadePercentual; }
    public void setAumentoProdutividadePercentual(BigDecimal aumentoProdutividadePercentual) { this.aumentoProdutividadePercentual = aumentoProdutividadePercentual; }
    public Integer getProgressoPercentual() { return progressoPercentual; }
    public void setProgressoPercentual(Integer progressoPercentual) { this.progressoPercentual = progressoPercentual; }
    public String getEstrategiaId() { return estrategiaId; }
    public void setEstrategiaId(String estrategiaId) { this.estrategiaId = estrategiaId; }
    public String getIdeiaId() { return ideiaId; }
    public void setIdeiaId(String ideiaId) { this.ideiaId = ideiaId; }
    public String getGestorResponsavelId() { return gestorResponsavelId; }
    public void setGestorResponsavelId(String gestorResponsavelId) { this.gestorResponsavelId = gestorResponsavelId; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }
    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
    public void setAtualizadoEm(LocalDateTime atualizadoEm) { this.atualizadoEm = atualizadoEm; }
    public List<ResultadoProjeto> getResultados() { return resultados; }
    public void setResultados(List<ResultadoProjeto> resultados) { this.resultados = resultados == null ? new ArrayList<>() : resultados; }
}
