package com.loopin.backend.bdd;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public final class ApiTestFixtures {
    static final String PASSWORD = "SenhaSegura123!";

    private ApiTestFixtures() {}

    public static Account createLeaderAndLogin(String bootstrapToken) {
        String email = uniqueEmail("lider");
        Response created = given()
                .contentType(ContentType.JSON)
                .header("X-Bootstrap-Token", bootstrapToken)
                .body(Map.of("nome", "Líder BDD", "email", email, "senha", PASSWORD))
                .when()
                .post("/auth/bootstrap-lider");
        created.then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/user-response.schema.json"));

        String id = created.jsonPath().getString("id");
        return new Account(email, login(email), id);
    }

    public static Account createManagerAndLogin(String leaderToken) {
        String email = uniqueEmail("gestor");
        Response created = given()
                .header("Authorization", "Bearer " + leaderToken)
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "nome", "Gestor BDD",
                        "email", email,
                        "senha", PASSWORD,
                        "perfil", "GESTOR"
                ))
                .when()
                .post("/usuarios/administracao");
        created.then()
                .statusCode(201)
                .body(matchesJsonSchemaInClasspath("schemas/user-response.schema.json"));

        return new Account(email, login(email), created.jsonPath().getString("id"));
    }

    public static Account createOperatorAndLogin() {
        String email = uniqueEmail("operador");
        Response created = given()
                .contentType(ContentType.JSON)
                .body(Map.of("nome", "Operador BDD", "email", email, "senha", PASSWORD))
                .when()
                .post("/usuarios");
        created.then()
                .statusCode(201)
                .body(matchesJsonSchemaInClasspath("schemas/user-response.schema.json"));

        return new Account(email, login(email), created.jsonPath().getString("id"));
    }

    public static String createStrategy(String leaderToken, String categoria, String campanha) {
        Response response = given()
                .header("Authorization", "Bearer " + leaderToken)
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "data", LocalDate.now().toString(),
                        "categoria", categoria,
                        "campanha", campanha,
                        "descricao", "Estratégia criada pela suíte BDD"
                ))
                .when()
                .post("/estrategias");
        response.then()
                .statusCode(201)
                .body(matchesJsonSchemaInClasspath("schemas/strategy.schema.json"));
        return response.jsonPath().getString("id");
    }

    private static String login(String email) {
        Response response = given()
                .contentType(ContentType.JSON)
                .body(Map.of("email", email, "senha", PASSWORD))
                .when()
                .post("/auth/login");
        response.then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/login-response.schema.json"));
        return response.jsonPath().getString("token");
    }

    private static String uniqueEmail(String prefix) {
        return "bdd-" + prefix + "-" + UUID.randomUUID() + "@example.com";
    }

    public record Account(String email, String token, String id) {}
}
