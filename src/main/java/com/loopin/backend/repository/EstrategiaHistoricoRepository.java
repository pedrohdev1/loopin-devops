package com.loopin.backend.repository;

import com.loopin.backend.model.EstrategiaHistorico;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface EstrategiaHistoricoRepository extends MongoRepository<EstrategiaHistorico, String> {
    List<EstrategiaHistorico> findByEstrategiaIdOrderByRegistradoEmDesc(String estrategiaId);
    List<EstrategiaHistorico> findAllByOrderByRegistradoEmDesc();
}
