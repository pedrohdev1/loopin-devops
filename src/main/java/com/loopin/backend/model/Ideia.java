package com.loopin.backend.model;

import jakarta.validation.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "ideias")
public class Ideia {
    @Id
    private String id;

    @NotBlank(message = "O título é obrigatório")
    private String titulo;
    @NotBlank(message = "A descrição é obrigatória")
    private String descricao;
    private String status;
    private String prioridade;
    private String estrategiaId;
    private String usuarioId;
    private LocalDate dataCriacao;
    private Integer pontuacaoIa;
    private String justificativaIa;
    private String comentarioGestor;

    public Ideia() {}

    public Ideia(String titulo, String descricao, String status, String prioridade,
                 String estrategiaId, String usuarioId, LocalDate dataCriacao) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.status = status;
        this.prioridade = prioridade;
        this.estrategiaId = estrategiaId;
        this.usuarioId = usuarioId;
        this.dataCriacao = dataCriacao;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPrioridade() { return prioridade; }
    public void setPrioridade(String prioridade) { this.prioridade = prioridade; }
    public String getEstrategiaId() { return estrategiaId; }
    public void setEstrategiaId(String estrategiaId) { this.estrategiaId = estrategiaId; }
    public String getUsuarioId() { return usuarioId; }
    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }
    public LocalDate getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDate dataCriacao) { this.dataCriacao = dataCriacao; }
    public Integer getPontuacaoIa() { return pontuacaoIa; }
    public void setPontuacaoIa(Integer pontuacaoIa) { this.pontuacaoIa = pontuacaoIa; }
    public String getJustificativaIa() { return justificativaIa; }
    public void setJustificativaIa(String justificativaIa) { this.justificativaIa = justificativaIa; }
    public String getComentarioGestor() { return comentarioGestor; }
    public void setComentarioGestor(String comentarioGestor) { this.comentarioGestor = comentarioGestor; }
}
