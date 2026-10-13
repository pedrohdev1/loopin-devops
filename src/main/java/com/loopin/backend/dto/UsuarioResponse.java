package com.loopin.backend.dto;

import com.loopin.backend.model.Perfil;
import com.loopin.backend.model.Usuario;

public record UsuarioResponse(String id, String nome, String email, Perfil perfil) {
    public static UsuarioResponse from(Usuario usuario) {
        return new UsuarioResponse(usuario.getId(), usuario.getNome(), usuario.getEmail(), usuario.getPerfil());
    }
}
