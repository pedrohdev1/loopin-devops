package com.loopin.backend.model;

import jakarta.validation.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "estrategias")
public class Estrategia {
    @Id
    private String id;

    private LocalDate data;

    @NotBlank(message = "A categoria é obrigatória")
    private String categoria;

    @NotBlank(message = "A campanha é obrigatória")
    private String campanha;

    private String descricao;
    private boolean vigente;

    public Estrategia() {}

    public Estrategia(String id, LocalDate data, String categoria, String campanha) {
        this.id = id;
        this.data = data;
        this.categoria = categoria;
        this.campanha = campanha;
        this.vigente = true;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public String getCampanha() { return campanha; }
    public void setCampanha(String campanha) { this.campanha = campanha; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public boolean isVigente() { return vigente; }
    public void setVigente(boolean vigente) { this.vigente = vigente; }
}
