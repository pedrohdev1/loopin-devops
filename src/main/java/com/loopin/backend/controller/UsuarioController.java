package com.loopin.backend.controller;

import com.loopin.backend.dto.CriarUsuarioAdministracaoRequest;
import com.loopin.backend.dto.RegistroUsuarioRequest;
import com.loopin.backend.dto.UsuarioResponse;
import com.loopin.backend.model.Usuario;
import com.loopin.backend.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) { this.usuarioService = usuarioService; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse registrarOperador(@Valid @RequestBody RegistroUsuarioRequest request) {
        // O perfil público é sempre OPERADOR; o cliente não pode se promover a gestor ou líder.
        return UsuarioResponse.from(usuarioService.registrarOperador(request));
    }

    @GetMapping("/me")
    public UsuarioResponse usuarioAtual(Authentication authentication) {
        return UsuarioResponse.from(usuarioService.buscarAutenticado(authentication.getName()));
    }

    @GetMapping
    public List<UsuarioResponse> listarTodos() {
        return usuarioService.listarTodos().stream().map(UsuarioResponse::from).toList();
    }

    @GetMapping("/{id}")
    public UsuarioResponse buscarPorId(@PathVariable String id) {
        return UsuarioResponse.from(usuarioService.buscarPorId(id));
    }

    @PostMapping("/administracao")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse criarPelaLideranca(@Valid @RequestBody CriarUsuarioAdministracaoRequest request) {
        return UsuarioResponse.from(usuarioService.criarPelaLideranca(request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable String id, Authentication authentication) {
        Usuario atual = usuarioService.buscarAutenticado(authentication.getName());
        if (atual.getId().equals(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Não é permitido excluir a própria conta autenticada");
        }
        usuarioService.excluir(id);
    }
}
