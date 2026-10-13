package com.loopin.backend.dto;

public record AiPontuacaoResponse(String ideiaId, int pontuacao, String prioridadeSugerida,
                                  String justificativa, String modelo) {}
