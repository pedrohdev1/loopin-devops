package com.loopin.backend.bdd;

import io.cucumber.java.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

public class TestDatabaseHooks {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Before(order = 0)
    public void limparBancoDeTestes() {
        mongoTemplate.getCollectionNames().forEach(mongoTemplate::dropCollection);
    }
}
