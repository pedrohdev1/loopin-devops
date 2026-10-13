package com.loopin.backend.service;

import com.loopin.backend.dto.DashboardResponse;
import com.loopin.backend.model.Estrategia;
import com.loopin.backend.model.Projeto;
import com.loopin.backend.repository.EstrategiaRepository;
import com.loopin.backend.repository.ProjetoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class RelatorioService {
    private final ProjetoRepository projetoRepository;
    private final EstrategiaRepository estrategiaRepository;

    public RelatorioService(ProjetoRepository projetoRepository, EstrategiaRepository estrategiaRepository) {
        this.projetoRepository = projetoRepository;
        this.estrategiaRepository = estrategiaRepository;
    }

    public DashboardResponse dashboard(String estrategiaId, String projetoId) {
        if (estrategiaId != null && !estrategiaId.isBlank() && projetoId != null && !projetoId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Use filtro por estratégia ou por projeto, não ambos ao mesmo tempo");
        }
        List<Projeto> projetos;
        if (projetoId != null && !projetoId.isBlank()) {
            projetos = List.of(projetoRepository.findById(projetoId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Projeto não encontrado")));
        } else if (estrategiaId != null && !estrategiaId.isBlank()) {
            if (!estrategiaRepository.existsById(estrategiaId)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Estratégia não encontrada");
            }
            projetos = projetoRepository.findByEstrategiaId(estrategiaId);
        } else {
            projetos = projetoRepository.findAll();
        }

        BigDecimal investimentoTotal = projetos.stream().map(Projeto::getInvestimento).map(this::zeroSeNulo)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal lucroTotal = projetos.stream().map(Projeto::getLucroObtido).map(this::zeroSeNulo)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal produtividadeMedia = media(projetos.stream().map(Projeto::getAumentoProdutividadePercentual).toList());
        BigDecimal progressoMedio = mediaInteira(projetos.stream().map(Projeto::getProgressoPercentual).toList());

        Map<String, Estrategia> estrategias = new LinkedHashMap<>();
        estrategiaRepository.findAll().forEach(e -> estrategias.put(e.getId(), e));
        Map<String, List<Projeto>> porEstrategia = new LinkedHashMap<>();
        for (Projeto projeto : projetos) {
            porEstrategia.computeIfAbsent(projeto.getEstrategiaId() == null ? "SEM_ESTRATEGIA" : projeto.getEstrategiaId(), ignored -> new ArrayList<>()).add(projeto);
        }

        List<DashboardResponse.ResumoEstrategia> resumosEstrategia = new ArrayList<>();
        for (Map.Entry<String, List<Projeto>> entry : porEstrategia.entrySet()) {
            List<Projeto> lista = entry.getValue();
            BigDecimal investimento = lista.stream().map(Projeto::getInvestimento).map(this::zeroSeNulo).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal lucro = lista.stream().map(Projeto::getLucroObtido).map(this::zeroSeNulo).reduce(BigDecimal.ZERO, BigDecimal::add);
            Estrategia estrategia = estrategias.get(entry.getKey());
            resumosEstrategia.add(new DashboardResponse.ResumoEstrategia(
                    entry.getKey(), estrategia == null ? "Estratégia removida" : estrategia.getCategoria(),
                    estrategia == null ? "-" : estrategia.getCampanha(), lista.size(), dinheiro(investimento), dinheiro(lucro), roi(lucro, investimento)));
        }

        List<DashboardResponse.ResumoProjeto> resumosProjeto = projetos.stream().map(p ->
                new DashboardResponse.ResumoProjeto(p.getId(), p.getNome(), p.getStatus(), p.getEtapa(),
                        dinheiro(p.getInvestimento()), dinheiro(p.getLucroObtido()), roi(p.getLucroObtido(), p.getInvestimento()),
                        p.getPrazo(), p.getProgressoPercentual())).toList();

        long emAndamento = projetos.stream().filter(p -> "EM_ANDAMENTO".equals(p.getStatus())).count();
        long concluidos = projetos.stream().filter(p -> "CONCLUIDO".equals(p.getStatus())).count();
        double prazoMedio = projetos.stream().map(Projeto::getPrazo).filter(Objects::nonNull)
                .mapToLong(prazo -> ChronoUnit.DAYS.between(LocalDate.now(), prazo)).average().orElse(0.0);
        return new DashboardResponse(projetos.size(), emAndamento, concluidos, dinheiro(investimentoTotal), dinheiro(lucroTotal),
                roi(lucroTotal, investimentoTotal), Math.round(prazoMedio), produtividadeMedia, progressoMedio, resumosEstrategia, resumosProjeto);
    }

    private BigDecimal roi(BigDecimal lucro, BigDecimal investimento) {
        BigDecimal base = zeroSeNulo(investimento);
        if (base.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        return zeroSeNulo(lucro).multiply(new BigDecimal("100")).divide(base, 2, RoundingMode.HALF_UP);
    }

    private BigDecimal media(List<BigDecimal> valores) {
        List<BigDecimal> validos = valores.stream().filter(Objects::nonNull).toList();
        if (validos.isEmpty()) return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = validos.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        return total.divide(BigDecimal.valueOf(validos.size()), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal mediaInteira(List<Integer> valores) {
        List<BigDecimal> convertidos = valores.stream().filter(Objects::nonNull).map(BigDecimal::valueOf).toList();
        return media(convertidos);
    }

    private BigDecimal dinheiro(BigDecimal value) { return zeroSeNulo(value).setScale(2, RoundingMode.HALF_UP); }
    private BigDecimal zeroSeNulo(BigDecimal value) { return value == null ? BigDecimal.ZERO : value; }
}
