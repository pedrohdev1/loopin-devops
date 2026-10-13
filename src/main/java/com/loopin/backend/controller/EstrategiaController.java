package com.loopin.backend.controller;

import com.loopin.backend.model.Estrategia;
import com.loopin.backend.model.EstrategiaHistorico;
import com.loopin.backend.service.EstrategiaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/estrategias")
public class EstrategiaController {
    private final EstrategiaService estrategiaService;

    public EstrategiaController(EstrategiaService estrategiaService) { this.estrategiaService = estrategiaService; }

    @GetMapping
    public List<Estrategia> listarTodas() { return estrategiaService.listarTodas(); }

    @GetMapping("/vigente")
    public Estrategia buscarVigente() { return estrategiaService.buscarVigente(); }

    @GetMapping("/historico")
    public List<EstrategiaHistorico> historico(@RequestParam(required = false) String estrategiaId) {
        return estrategiaService.listarHistorico(estrategiaId);
    }

    @GetMapping("/{id}")
    public Estrategia buscarPorId(@PathVariable String id) { return estrategiaService.buscarPorId(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Estrategia salvar(@Valid @RequestBody Estrategia estrategia, Authentication authentication) {
        return estrategiaService.salvar(estrategia, authentication.getName());
    }

    @PutMapping("/{id}")
    public Estrategia atualizar(@PathVariable String id, @Valid @RequestBody Estrategia estrategia,
                                Authentication authentication) {
        return estrategiaService.atualizar(id, estrategia, authentication.getName());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable String id, Authentication authentication) {
        estrategiaService.excluir(id, authentication.getName());
    }
}
