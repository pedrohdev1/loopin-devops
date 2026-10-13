package com.loopin.backend.service;

import com.loopin.backend.dto.AiPontuacaoResponse;
import com.loopin.backend.model.Estrategia;
import com.loopin.backend.model.Ideia;
import com.loopin.backend.repository.EstrategiaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AiService {

    private final IdeiaService ideiaService;
    private final EstrategiaRepository estrategiaRepository;
    private final String apiKey;
    private final String baseUrl;
    private final String model;

    public AiService(
            IdeiaService ideiaService,
            EstrategiaRepository estrategiaRepository,
            @Value("${ai.openrouter.api-key:}") String apiKey,
            @Value("${ai.openrouter.base-url:https://openrouter.ai/api/v1}")
            String baseUrl,
            @Value("${ai.openrouter.model:openrouter/free}") String model
    ) {
        this.ideiaService = ideiaService;
        this.estrategiaRepository = estrategiaRepository;
        this.apiKey = apiKey == null ? "" : apiKey.trim();
        this.baseUrl = baseUrl;
        this.model = model;
    }

    public AiPontuacaoResponse pontuarIdeia(String ideiaId) {

        if (apiKey.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Integração de IA não configurada. " +
                            "Defina OPENROUTER_API_KEY e OPENROUTER_MODEL."
            );
        }

        Ideia ideia = ideiaService.buscarParaIa(ideiaId);

        Estrategia estrategia = ideia.getEstrategiaId() == null
                ? null
                : estrategiaRepository.findById(ideia.getEstrategiaId())
                .orElse(null);

        String categoria = estrategia == null
                ? "não informada"
                : estrategia.getCategoria();

        String campanha = estrategia == null
                ? "não informada"
                : estrategia.getCampanha();

        String prompt =
                "Avalie uma ideia de inovação empresarial. " +
                        "Retorne somente um objeto JSON válido com as chaves " +
                        "pontuacao (inteiro de 0 a 100), " +
                        "prioridade (BAIXA, MEDIA ou ALTA) e " +
                        "justificativa (texto curto). " +
                        "Considere impacto para o negócio, viabilidade, clareza, " +
                        "mensurabilidade e alinhamento estratégico. " +
                        "Não invente dados e deixe claro que a pontuação é apenas " +
                        "uma recomendação, não uma decisão final.\n" +
                        "Título: " + ideia.getTitulo() + "\n" +
                        "Descrição: " + ideia.getDescricao() + "\n" +
                        "Categoria estratégica: " + categoria + "\n" +
                        "Campanha estratégica associada à ideia: " + campanha;

        Map<String, Object> payload = Map.of(
                "model", model,
                "temperature", 0.2,
                "max_tokens", 350,
                "messages", List.of(
                        Map.of(
                                "role", "system",
                                "content",
                                "Você é um avaliador auxiliar de ideias de inovação. " +
                                        "Seja criterioso, neutro e transparente."
                        ),
                        Map.of(
                                "role", "user",
                                "content", prompt
                        )
                )
        );

        try {
            Map<?, ?> resposta = RestClient.builder()
                    .baseUrl(baseUrl)
                    .build()
                    .post()
                    .uri("/chat/completions")
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .header("HTTP-Referer", "https://loopin.local")
                    .header("X-OpenRouter-Title", "Loopin")
                    .body(payload)
                    .retrieve()
                    .body(Map.class);

            String conteudo = extrairConteudo(resposta);

            int score = extrairInteiro(conteudo, "pontuacao", -1);

            String prioridade = extrairTexto(
                    conteudo,
                    "prioridade",
                    "MEDIA"
            ).toUpperCase();

            String justificativa = extrairTexto(
                    conteudo,
                    "justificativa",
                    "A IA não forneceu uma justificativa."
            );

            Ideia atualizada = ideiaService.registrarPontuacaoIa(
                    ideiaId,
                    score,
                    prioridade,
                    justificativa
            );

            return new AiPontuacaoResponse(
                    atualizada.getId(),
                    score,
                    atualizada.getPrioridade(),
                    justificativa,
                    model
            );

        } catch (RestClientResponseException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Falha ao consultar o provedor de IA (HTTP " +
                            e.getStatusCode().value() +
                            "). Verifique a chave, o modelo e o limite de uso."
            );

        } catch (ResponseStatusException e) {
            throw e;

        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Não foi possível interpretar a resposta do provedor " +
                            "de IA. Verifique os detalhes da integração e tente novamente."
            );
        }
    }

    private String extrairConteudo(Map<?, ?> resposta) {

        if (resposta == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "O provedor de IA retornou uma resposta nula."
            );
        }

        if (!(resposta.get("choices") instanceof List<?> choices)
                || choices.isEmpty()) {

            Object erro = resposta.get("error");

            String detalhes = erro == null
                    ? "Chaves recebidas: " + resposta.keySet()
                    : erro.toString();

            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Resposta da IA sem choices. " + detalhes
            );
        }

        Object first = choices.get(0);

        if (!(first instanceof Map<?, ?> choice)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Formato de choice inesperado na resposta da IA."
            );
        }

        if (!(choice.get("message") instanceof Map<?, ?> message)) {
            Object motivo = choice.get("finish_reason");

            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Resposta sem message. Motivo: " + motivo
            );
        }

        Object content = message.get("content");

        if (content instanceof String texto && !texto.isBlank()) {
            return texto;
        }

        Object motivo = choice.get("finish_reason");
        Object recusa = message.get("refusal");

        throw new ResponseStatusException(
                HttpStatus.BAD_GATEWAY,
                "Resposta sem conteúdo textual. " +
                        "Motivo: " + motivo +
                        "; recusa: " +
                        (recusa == null ? "não informada" : recusa)
        );
    }

    private int extrairInteiro(
            String json,
            String chave,
            int padrao
    ) {
        Matcher matcher = Pattern.compile(
                "\"" + Pattern.quote(chave) +
                        "\"\\s*:\\s*(-?\\d+)",
                Pattern.CASE_INSENSITIVE
        ).matcher(json);

        if (!matcher.find()) {
            return padrao;
        }

        try {
            return Integer.parseInt(matcher.group(1));
        } catch (NumberFormatException e) {
            return padrao;
        }
    }

    private String extrairTexto(
            String json,
            String chave,
            String padrao
    ) {
        Matcher matcher = Pattern.compile(
                "\"" + Pattern.quote(chave) +
                        "\"\\s*:\\s*\"((?:\\\\.|[^\"\\\\])*)\"",
                Pattern.CASE_INSENSITIVE | Pattern.DOTALL
        ).matcher(json);

        if (!matcher.find()) {
            return padrao;
        }

        return matcher.group(1)
                .replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\t", "\t")
                .replace("\\\"", "\"")
                .replace("\\\\", "\\");
    }
}