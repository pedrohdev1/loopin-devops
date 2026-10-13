package com.loopin.backend.dto;

import com.loopin.backend.model.Perfil;

public record LoginResponse(String token, String id, String nome, String email, Perfil perfil, String tipoToken) {
    public LoginResponse(String token, String id, String nome, String email, Perfil perfil) {
        this(token, id, nome, email, perfil, "Bearer");
    }
}
