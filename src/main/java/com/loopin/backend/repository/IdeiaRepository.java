package com.loopin.backend.repository;

import com.loopin.backend.model.Ideia;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface IdeiaRepository extends MongoRepository<Ideia, String> {

    List<Ideia> findByUsuarioId(String usuarioId);
    boolean existsByEstrategiaId(String estrategiaId);

}