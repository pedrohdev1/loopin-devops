package com.loopin.backend.repository;

import com.loopin.backend.model.Estrategia;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface EstrategiaRepository extends MongoRepository<Estrategia, String> {
    Optional<Estrategia> findFirstByVigenteTrue();
}
