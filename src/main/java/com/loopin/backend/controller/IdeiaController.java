package com.loopin.backend.controller;

import com.loopin.backend.dto.AvaliacaoIdeiaRequest;
import com.loopin.backend.dto.IdeiaRequest;
import com.loopin.backend.model.Ideia;
import com.loopin.backend.service.IdeiaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ideias")
public class IdeiaController {
    private final IdeiaService ideiaService;

    public IdeiaController(IdeiaService ideiaService) { this.ideiaService = ideiaService; }

    @GetMapping("/minhas")
    public List<Ideia> listarMinhas(Authentication authentication) {
        return ideiaService.listarMinhas(authentication.getName());
    }

    @GetMapping
    public List<Ideia> listarParaGestao() { return ideiaService.listarParaGestao(); }

    @GetMapping("/{id}")
    public Ideia buscarPorId(@PathVariable String id, Authentication authentication) {
        return ideiaService.buscarPorId(id, authentication.getName());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Ideia criar(@Valid @RequestBody IdeiaRequest request, Authentication authentication) {
        return ideiaService.criar(request, authentication.getName());
    }

    @PutMapping("/{id}")
    public Ideia atualizar(@PathVariable String id, @Valid @RequestBody IdeiaRequest request,
                           Authentication authentication) {
        return ideiaService.atualizar(id, request, authentication.getName());
    }

    @PatchMapping("/{id}/avaliacao")
    public Ideia avaliar(@PathVariable String id, @Valid @RequestBody AvaliacaoIdeiaRequest request) {
        return ideiaService.avaliar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable String id, Authentication authentication) {
        ideiaService.excluir(id, authentication.getName());
    }
}
