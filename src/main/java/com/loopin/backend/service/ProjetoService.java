package com.loopin.backend.service;

import com.loopin.backend.dto.ProjetoRequest;
import com.loopin.backend.dto.AtualizacaoProjetoRequest;
import com.loopin.backend.dto.ResultadoProjetoRequest;
import com.loopin.backend.model.Estrategia;
import com.loopin.backend.model.Ideia;
import com.loopin.backend.model.Projeto;
import com.loopin.backend.model.ResultadoProjeto;
import com.loopin.backend.model.Usuario;
import com.loopin.backend.repository.EstrategiaRepository;
import com.loopin.backend.repository.IdeiaRepository;
import com.loopin.backend.repository.ProjetoRepository;
import com.loopin.backend.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class ProjetoService {
    private static final Set<String> STATUS_VALIDOS = Set.of("PLANEJADO", "EM_ANDAMENTO", "CONCLUIDO", "SUSPENSO", "CANCELADO");

    private final ProjetoRepository projetoRepository;
    private final UsuarioRepository usuarioRepository;
    private final EstrategiaRepository estrategiaRepository;
    private final IdeiaRepository ideiaRepository;

    public ProjetoService(ProjetoRepository projetoRepository, UsuarioRepository usuarioRepository,
                          EstrategiaRepository estrategiaRepository, IdeiaRepository ideiaRepository) {
        this.projetoRepository = projetoRepository;
        this.usuarioRepository = usuarioRepository;
        this.estrategiaRepository = estrategiaRepository;
        this.ideiaRepository = ideiaRepository;
    }

    public List<Projeto> listarTodos() { return projetoRepository.findAll(); }

    public Projeto buscarPorId(String id) {
        return projetoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Projeto não encontrado"));
    }

    public Projeto criar(ProjetoRequest request, String emailGestor) {
        Usuario gestor = obterGestor(emailGestor);
        validarStatus(request.status());
        Estrategia estrategia = validarEstrategiaVigente(request.estrategiaId());
        validarIdeiaAprovada(request.ideiaId(), estrategia.getId());

        Projeto projeto = new Projeto();
        aplicarRequest(projeto, request);
        projeto.setGestorResponsavelId(gestor.getId());
        projeto.setCriadoEm(LocalDateTime.now());
        projeto.setAtualizadoEm(LocalDateTime.now());
        projeto.setLucroObtido(BigDecimal.ZERO);
        projeto.setEstrategiaId(estrategia.getId());
        return projetoRepository.save(projeto);
    }

    public Projeto atualizar(String id, AtualizacaoProjetoRequest request, String emailGestor) {
        obterGestor(emailGestor);
        Projeto projeto = buscarPorId(id);
        validarStatus(request.status());
        projeto.setNome(request.nome().trim());
        projeto.setDescricao(request.descricao().trim());
        projeto.setEtapa(request.etapa().trim());
        projeto.setStatus(request.status().trim().toUpperCase());
        projeto.setPrazo(request.prazo());
        if (request.aumentoProdutividadePercentual() != null) {
            projeto.setAumentoProdutividadePercentual(request.aumentoProdutividadePercentual());
        }
        if (request.progressoPercentual() != null) projeto.setProgressoPercentual(request.progressoPercentual());
        ajustarStatusConformeProgresso(projeto);
        projeto.setAtualizadoEm(LocalDateTime.now());
        return projetoRepository.save(projeto);
    }

    public void excluir(String id, String emailGestor) {
        obterGestor(emailGestor);
        projetoRepository.delete(buscarPorId(id));
    }

    public List<ResultadoProjeto> listarResultados(String id) {
        return buscarPorId(id).getResultados();
    }

    public Projeto adicionarResultado(String id, ResultadoProjetoRequest request, String emailGestor) {
        obterGestor(emailGestor);
        Projeto projeto = buscarPorId(id);
        ResultadoProjeto resultado = new ResultadoProjeto();
        resultado.setData(request.data() == null ? LocalDate.now() : request.data());
        resultado.setDescricao(request.descricao().trim());
        resultado.setInvestimentoAdicional(request.investimentoAdicional() == null ? BigDecimal.ZERO : request.investimentoAdicional());
        resultado.setLucroObtido(request.lucroObtido() == null ? BigDecimal.ZERO : request.lucroObtido());
        resultado.setAumentoProdutividadePercentual(request.aumentoProdutividadePercentual());
        resultado.setProgressoPercentual(request.progressoPercentual());
        if (projeto.getResultados() == null) projeto.setResultados(new java.util.ArrayList<>());
        projeto.getResultados().add(resultado);

        projeto.setInvestimento(valorOuZero(projeto.getInvestimento()).add(resultado.getInvestimentoAdicional()));
        projeto.setLucroObtido(valorOuZero(projeto.getLucroObtido()).add(resultado.getLucroObtido()));
        if (resultado.getAumentoProdutividadePercentual() != null) {
            projeto.setAumentoProdutividadePercentual(resultado.getAumentoProdutividadePercentual());
        }
        if (resultado.getProgressoPercentual() != null) {
            projeto.setProgressoPercentual(resultado.getProgressoPercentual());
        }
        projeto.setAtualizadoEm(LocalDateTime.now());
        ajustarStatusConformeProgresso(projeto);
        return projetoRepository.save(projeto);
    }

    private void aplicarRequest(Projeto projeto, ProjetoRequest request) {
        String status = request.status().trim().toUpperCase();
        projeto.setNome(request.nome().trim());
        projeto.setDescricao(request.descricao().trim());
        projeto.setEtapa(request.etapa().trim());
        projeto.setStatus(status);
        projeto.setInvestimento(request.investimento());
        projeto.setPrazo(request.prazo());
        projeto.setAumentoProdutividadePercentual(request.aumentoProdutividadePercentual() == null ? BigDecimal.ZERO : request.aumentoProdutividadePercentual());
        projeto.setProgressoPercentual(request.progressoPercentual() == null ? 0 : request.progressoPercentual());
        projeto.setIdeiaId(request.ideiaId());
    }

    private void validarStatus(String valor) {
        if (valor == null || !STATUS_VALIDOS.contains(valor.trim().toUpperCase())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status inválido. Use PLANEJADO, EM_ANDAMENTO, CONCLUIDO, SUSPENSO ou CANCELADO");
        }
    }

    private Estrategia validarEstrategiaVigente(String estrategiaId) {
        Estrategia estrategia = estrategiaRepository.findById(estrategiaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estratégia informada não existe"));
        if (!estrategia.isVigente()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O projeto deve estar vinculado à estratégia vigente");
        }
        return estrategia;
    }

    private void validarIdeiaAprovada(String ideiaId, String estrategiaId) {
        if (ideiaId == null || ideiaId.isBlank()) return;
        Ideia ideia = ideiaRepository.findById(ideiaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ideia informada não existe"));
        if (!"APROVADA".equals(ideia.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Só é possível vincular projetos a ideias aprovadas");
        }
        if (!java.util.Objects.equals(ideia.getEstrategiaId(), estrategiaId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "A ideia deve estar vinculada à mesma estratégia vigente do projeto");
        }
    }

    private void ajustarStatusConformeProgresso(Projeto projeto) {
        Integer progresso = projeto.getProgressoPercentual();
        if (Integer.valueOf(100).equals(progresso)) {
            projeto.setStatus("CONCLUIDO");
        } else if (progresso != null && progresso > 0 && "PLANEJADO".equals(projeto.getStatus())) {
            projeto.setStatus("EM_ANDAMENTO");
        }
    }

    private Usuario obterGestor(String email) {
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não encontrado"));
        if (usuario.getPerfil() != com.loopin.backend.model.Perfil.GESTOR) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Somente gestores podem gerenciar projetos");
        }
        return usuario;
    }

    private BigDecimal valorOuZero(BigDecimal valor) { return valor == null ? BigDecimal.ZERO : valor; }
}
