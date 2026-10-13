package com.loopin.backend.service;

import com.loopin.backend.dto.AvaliacaoIdeiaRequest;
import com.loopin.backend.dto.IdeiaRequest;
import com.loopin.backend.model.Estrategia;
import com.loopin.backend.model.Ideia;
import com.loopin.backend.model.Perfil;
import com.loopin.backend.model.Usuario;
import com.loopin.backend.repository.EstrategiaRepository;
import com.loopin.backend.repository.IdeiaRepository;
import com.loopin.backend.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.Objects;

@Service
public class IdeiaService {
    private static final Set<String> STATUS_VALIDOS = Set.of("PENDENTE", "EM_ANALISE", "APROVADA", "REJEITADA");
    private static final Set<String> PRIORIDADES_VALIDAS = Set.of("BAIXA", "MEDIA", "ALTA");

    private final IdeiaRepository ideiaRepository;
    private final UsuarioRepository usuarioRepository;
    private final EstrategiaRepository estrategiaRepository;

    public IdeiaService(IdeiaRepository ideiaRepository, UsuarioRepository usuarioRepository,
                        EstrategiaRepository estrategiaRepository) {
        this.ideiaRepository = ideiaRepository;
        this.usuarioRepository = usuarioRepository;
        this.estrategiaRepository = estrategiaRepository;
    }

    public List<Ideia> listarParaGestao() {
        return ideiaRepository.findAll();
    }

    public List<Ideia> listarMinhas(String email) {
        Usuario usuario = obterUsuario(email);
        return ideiaRepository.findByUsuarioId(usuario.getId());
    }

    public Ideia buscarPorId(String id, String email) {
        Ideia ideia = encontrar(id);
        Usuario usuario = obterUsuario(email);
        if (usuario.getPerfil() == Perfil.OPERADOR && !Objects.equals(ideia.getUsuarioId(), usuario.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você só pode consultar suas próprias ideias");
        }
        return ideia;
    }

    public Ideia criar(IdeiaRequest request, String email) {
        Usuario usuario = obterUsuario(email);
        if (usuario.getPerfil() != Perfil.OPERADOR) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Somente operadores podem cadastrar ideias");
        }
        Estrategia vigente = estrategiaRepository.findFirstByVigenteTrue()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Cadastre uma estratégia vigente antes de registrar ideias"));
        Ideia ideia = new Ideia();
        ideia.setTitulo(request.titulo().trim());
        ideia.setDescricao(request.descricao().trim());
        ideia.setStatus("PENDENTE");
        ideia.setPrioridade("NAO_AVALIADA");
        ideia.setEstrategiaId(vigente.getId());
        ideia.setUsuarioId(usuario.getId());
        ideia.setDataCriacao(LocalDate.now());
        return ideiaRepository.save(ideia);
    }

    public Ideia atualizar(String id, IdeiaRequest request, String email) {
        Ideia ideia = encontrar(id);
        Usuario usuario = obterUsuario(email);
        verificarProprietario(usuario, ideia);
        if (!"PENDENTE".equals(ideia.getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ideias que já estão em análise não podem ser editadas pelo operador");
        }
        ideia.setTitulo(request.titulo().trim());
        ideia.setDescricao(request.descricao().trim());
        return ideiaRepository.save(ideia);
    }

    public void excluir(String id, String email) {
        Ideia ideia = encontrar(id);
        verificarProprietario(obterUsuario(email), ideia);
        if (!"PENDENTE".equals(ideia.getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Somente ideias pendentes podem ser excluídas pelo operador");
        }
        ideiaRepository.delete(ideia);
    }

    public Ideia avaliar(String id, AvaliacaoIdeiaRequest request) {
        Ideia ideia = encontrar(id);
        String status = request.status().trim().toUpperCase();
        String prioridade = request.prioridade().trim().toUpperCase();
        if (!STATUS_VALIDOS.contains(status)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status inválido. Use PENDENTE, EM_ANALISE, APROVADA ou REJEITADA");
        }
        if (!PRIORIDADES_VALIDAS.contains(prioridade)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Prioridade inválida. Use BAIXA, MEDIA ou ALTA");
        }
        ideia.setStatus(status);
        ideia.setPrioridade(prioridade);
        ideia.setComentarioGestor(request.comentarioGestor());
        return ideiaRepository.save(ideia);
    }

    public Ideia buscarParaIa(String id) { return encontrar(id); }

    public Ideia registrarPontuacaoIa(String id, int pontuacao, String prioridade, String justificativa) {
        Ideia ideia = encontrar(id);
        if (pontuacao < 0 || pontuacao > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "A IA devolveu uma pontuação inválida");
        }
        String normalizada = prioridade == null ? "MEDIA" : prioridade.trim().toUpperCase();
        if (!PRIORIDADES_VALIDAS.contains(normalizada)) normalizada = "MEDIA";
        ideia.setPontuacaoIa(pontuacao);
        ideia.setJustificativaIa(justificativa);
        ideia.setPrioridade(normalizada);
        if ("PENDENTE".equals(ideia.getStatus())) ideia.setStatus("EM_ANALISE");
        return ideiaRepository.save(ideia);
    }

    private Ideia encontrar(String id) {
        return ideiaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ideia não encontrada"));
    }

    private Usuario obterUsuario(String email) {
        return usuarioRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não encontrado"));
    }

    private void verificarProprietario(Usuario usuario, Ideia ideia) {
        if (usuario.getPerfil() != Perfil.OPERADOR || !Objects.equals(ideia.getUsuarioId(), usuario.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você só pode alterar ou excluir suas próprias ideias");
        }
    }
}
