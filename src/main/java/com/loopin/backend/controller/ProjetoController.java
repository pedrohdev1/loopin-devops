package com.loopin.backend.controller;

import com.loopin.backend.dto.ProjetoRequest;
import com.loopin.backend.dto.AtualizacaoProjetoRequest;
import com.loopin.backend.dto.ResultadoProjetoRequest;
import com.loopin.backend.model.Projeto;
import com.loopin.backend.model.ResultadoProjeto;
import com.loopin.backend.service.ProjetoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projetos")
public class ProjetoController {
    private final ProjetoService projetoService;

    public ProjetoController(ProjetoService projetoService) { this.projetoService = projetoService; }

    @GetMapping
    public List<Projeto> listarTodos() { return projetoService.listarTodos(); }

    @GetMapping("/{id}")
    public Projeto buscarPorId(@PathVariable String id) { return projetoService.buscarPorId(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Projeto criar(@Valid @RequestBody ProjetoRequest request, Authentication authentication) {
        return projetoService.criar(request, authentication.getName());
    }

    @PutMapping("/{id}")
    public Projeto atualizar(@PathVariable String id, @Valid @RequestBody AtualizacaoProjetoRequest request,
                             Authentication authentication) {
        return projetoService.atualizar(id, request, authentication.getName());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable String id, Authentication authentication) {
        projetoService.excluir(id, authentication.getName());
    }

    @GetMapping("/{id}/resultados")
    public List<ResultadoProjeto> listarResultados(@PathVariable String id) {
        return projetoService.listarResultados(id);
    }

    @PostMapping("/{id}/resultados")
    @ResponseStatus(HttpStatus.CREATED)
    public Projeto adicionarResultado(@PathVariable String id, @Valid @RequestBody ResultadoProjetoRequest request,
                                     Authentication authentication) {
        return projetoService.adicionarResultado(id, request, authentication.getName());
    }
}
