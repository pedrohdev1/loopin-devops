package com.loopin.backend.service;

import com.loopin.backend.dto.CriarUsuarioAdministracaoRequest;
import com.loopin.backend.dto.RegistroUsuarioRequest;
import com.loopin.backend.model.Perfil;
import com.loopin.backend.model.Usuario;
import com.loopin.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final String bootstrapToken;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder,
                          @Value("${app.bootstrap-token:}") String bootstrapToken) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.bootstrapToken = bootstrapToken == null ? "" : bootstrapToken;
    }

    public Usuario registrarOperador(RegistroUsuarioRequest request) {
        validarEmailDisponivel(request.email());
        Usuario usuario = new Usuario();
        usuario.setNome(request.nome().trim());
        usuario.setEmail(normalizarEmail(request.email()));
        usuario.setSenha(passwordEncoder.encode(request.senha()));
        usuario.setPerfil(Perfil.OPERADOR);
        return usuarioRepository.save(usuario);
    }

    public Usuario criarPelaLideranca(CriarUsuarioAdministracaoRequest request) {
        validarEmailDisponivel(request.email());
        Usuario usuario = new Usuario();
        usuario.setNome(request.nome().trim());
        usuario.setEmail(normalizarEmail(request.email()));
        usuario.setSenha(passwordEncoder.encode(request.senha()));
        usuario.setPerfil(request.perfil());
        return usuarioRepository.save(usuario);
    }

    public Usuario bootstrapLider(RegistroUsuarioRequest request, String tokenFornecido) {
        if (bootstrapToken.isBlank()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "A criação inicial de líder está desabilitada. Configure APP_BOOTSTRAP_TOKEN.");
        }
        if (tokenFornecido == null || !MessageDigest.isEqual(
                bootstrapToken.getBytes(StandardCharsets.UTF_8),
                tokenFornecido.getBytes(StandardCharsets.UTF_8))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token de inicialização inválido");
        }
        if (usuarioRepository.existsByPerfil(Perfil.LIDER)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um líder cadastrado; use o login normal.");
        }
        validarEmailDisponivel(request.email());
        Usuario usuario = new Usuario();
        usuario.setNome(request.nome().trim());
        usuario.setEmail(normalizarEmail(request.email()));
        usuario.setSenha(passwordEncoder.encode(request.senha()));
        usuario.setPerfil(Perfil.LIDER);
        return usuarioRepository.save(usuario);
    }

    public Usuario buscarAutenticado(String email) {
        return usuarioRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário autenticado não encontrado"));
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Usuario buscarPorId(String id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
    }

    public void excluir(String id) {
        if (!usuarioRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado");
        }
        usuarioRepository.deleteById(id);
    }

    private void validarEmailDisponivel(String email) {
        if (usuarioRepository.existsByEmailIgnoreCase(normalizarEmail(email))) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe usuário cadastrado com esse e-mail");
        }
    }

    private String normalizarEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }
}
