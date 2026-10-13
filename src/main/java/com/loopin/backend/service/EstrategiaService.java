package com.loopin.backend.service;

import com.loopin.backend.model.Estrategia;
import com.loopin.backend.model.EstrategiaHistorico;
import com.loopin.backend.repository.EstrategiaHistoricoRepository;
import com.loopin.backend.repository.EstrategiaRepository;
import com.loopin.backend.repository.IdeiaRepository;
import com.loopin.backend.repository.ProjetoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
public class EstrategiaService {
    private final EstrategiaRepository estrategiaRepository;
    private final EstrategiaHistoricoRepository historicoRepository;
    private final IdeiaRepository ideiaRepository;
    private final ProjetoRepository projetoRepository;

    public EstrategiaService(EstrategiaRepository estrategiaRepository, EstrategiaHistoricoRepository historicoRepository,
                             IdeiaRepository ideiaRepository, ProjetoRepository projetoRepository) {
        this.estrategiaRepository = estrategiaRepository;
        this.historicoRepository = historicoRepository;
        this.ideiaRepository = ideiaRepository;
        this.projetoRepository = projetoRepository;
    }

    public List<Estrategia> listarTodas() { return estrategiaRepository.findAll(); }

    public Estrategia buscarPorId(String id) {
        return estrategiaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estratégia não encontrada"));
    }

    public Estrategia buscarVigente() {
        return estrategiaRepository.findFirstByVigenteTrue()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Não há estratégia vigente cadastrada"));
    }

    public Estrategia salvar(Estrategia estrategia, String emailResponsavel) {
        if (estrategia.getData() == null) estrategia.setData(LocalDate.now());
        if (estrategia.getId() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Não informe o campo id ao criar uma estratégia; use PUT /estrategias/{id} para atualizar");
        }
        // Cada nova estratégia aprovada pela liderança passa a ser a vigente; a anterior fica no histórico.
        estrategia.setVigente(true);
        desativarOutrasVigentes(null, emailResponsavel);
        Estrategia salva = estrategiaRepository.save(estrategia);
        historicoRepository.save(new EstrategiaHistorico(salva, "CRIADA", emailResponsavel));
        return salva;
    }

    public Estrategia atualizar(String id, Estrategia entrada, String emailResponsavel) {
        Estrategia existente = buscarPorId(id);
        historicoRepository.save(new EstrategiaHistorico(existente, "ANTES_DA_ATUALIZACAO", emailResponsavel));
        existente.setData(entrada.getData() == null ? existente.getData() : entrada.getData());
        existente.setCategoria(entrada.getCategoria());
        existente.setCampanha(entrada.getCampanha());
        existente.setDescricao(entrada.getDescricao());
        // Uma estratégia vigente continua vigente quando atualizada; para trocar a vigente, crie uma nova.
        existente.setVigente(existente.isVigente() || entrada.isVigente());
        if (existente.isVigente()) desativarOutrasVigentes(id, emailResponsavel);
        Estrategia salva = estrategiaRepository.save(existente);
        historicoRepository.save(new EstrategiaHistorico(salva, "ATUALIZADA", emailResponsavel));
        return salva;
    }

    public void excluir(String id, String emailResponsavel) {
        Estrategia existente = buscarPorId(id);
        if (existente.isVigente()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Não é possível excluir a estratégia vigente. Cadastre uma nova estratégia antes.");
        }
        if (ideiaRepository.existsByEstrategiaId(id) || projetoRepository.existsByEstrategiaId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A estratégia possui ideias ou projetos vinculados e não pode ser excluída");
        }
        historicoRepository.save(new EstrategiaHistorico(existente, "EXCLUIDA", emailResponsavel));
        estrategiaRepository.deleteById(id);
    }

    public List<EstrategiaHistorico> listarHistorico(String estrategiaId) {
        if (estrategiaId == null || estrategiaId.isBlank()) return historicoRepository.findAllByOrderByRegistradoEmDesc();
        return historicoRepository.findByEstrategiaIdOrderByRegistradoEmDesc(estrategiaId);
    }

    private void desativarOutrasVigentes(String idQuePodePermanecer, String emailResponsavel) {
        for (Estrategia atual : estrategiaRepository.findAll()) {
            if (atual.isVigente() && (idQuePodePermanecer == null || !atual.getId().equals(idQuePodePermanecer))) {
                atual.setVigente(false);
                estrategiaRepository.save(atual);
                historicoRepository.save(new EstrategiaHistorico(atual, "ENCERRADA_POR_NOVA_VIGENCIA", emailResponsavel));
            }
        }
    }
}
