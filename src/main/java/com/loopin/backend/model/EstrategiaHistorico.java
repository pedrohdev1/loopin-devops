package com.loopin.backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDate;

@Document(collection = "estrategias_historico")
public class EstrategiaHistorico {
    @Id
    private String id;
    private String estrategiaId;
    private LocalDate data;
    private String categoria;
    private String campanha;
    private boolean vigente;
    private String operacao;
    private Instant registradoEm;
    private String alteradoPor;

    public EstrategiaHistorico() {}

    public EstrategiaHistorico(Estrategia estrategia, String operacao, String alteradoPor) {
        this.estrategiaId = estrategia.getId();
        this.data = estrategia.getData();
        this.categoria = estrategia.getCategoria();
        this.campanha = estrategia.getCampanha();
        this.vigente = estrategia.isVigente();
        this.operacao = operacao;
        this.registradoEm = Instant.now();
        this.alteradoPor = alteradoPor;
    }

    public String getId() { return id; }
    public String getEstrategiaId() { return estrategiaId; }
    public LocalDate getData() { return data; }
    public String getCategoria() { return categoria; }
    public String getCampanha() { return campanha; }
    public boolean isVigente() { return vigente; }
    public String getOperacao() { return operacao; }
    public Instant getRegistradoEm() { return registradoEm; }
    public String getAlteradoPor() { return alteradoPor; }
}
