package com.loopin.backend.controller;

import com.loopin.backend.dto.LoginRequest;
import com.loopin.backend.dto.LoginResponse;
import com.loopin.backend.dto.RegistroUsuarioRequest;
import com.loopin.backend.dto.UsuarioResponse;
import com.loopin.backend.service.AuthService;
import com.loopin.backend.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final UsuarioService usuarioService;

    public AuthController(AuthService authService, UsuarioService usuarioService) {
        this.authService = authService;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/bootstrap-lider")
    public UsuarioResponse bootstrapLider(@RequestHeader(value = "X-Bootstrap-Token", required = false) String token,
                                          @Valid @RequestBody RegistroUsuarioRequest request) {
        return UsuarioResponse.from(usuarioService.bootstrapLider(request, token));
    }
}
