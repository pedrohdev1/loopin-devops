package com.loopin.backend.repository;

import com.loopin.backend.model.Projeto;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProjetoRepository extends MongoRepository<Projeto, String> {
    List<Projeto> findByEstrategiaId(String estrategiaId);
    List<Projeto> findByGestorResponsavelId(String gestorResponsavelId);
    boolean existsByEstrategiaId(String estrategiaId);
}
