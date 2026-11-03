package com.loopin.backend.bdd;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;

public class StrategyApiSteps {
    @Value("${app.bootstrap-token}")
    private String bootstrapToken;

    private String leaderToken;
    private String firstStrategyId;
    private String currentStrategyId;
    private Response response;

    @Dado("uma liderança autenticada para testar estratégias")
    public void liderAutenticado() {
        leaderToken = ApiTestFixtures.createLeaderAndLogin(bootstrapToken).token();
    }

    @Quando("consulto a saúde da API")
    public void consultarSaude() {
        response = given().when().get("/health");
    }

    @Entao("a saúde responde conforme o contrato")
    public void validarSaude() {
        response.then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/health-response.schema.json"))
                .body("status", equalTo("UP"))
                .body("application", equalTo("loopin-backend"));
    }

    @Quando("crio a estratégia inicial")
    public void criarEstrategiaInicial() {
        response = given()
                .header("Authorization", "Bearer " + leaderToken)
                .contentType(ContentType.JSON)
                .body(payload("Eficiência operacional", "Ciclo sustentável 2026"))
                .when()
                .post("/estrategias");
    }

    @Entao("a estratégia inicial é criada")
    public void validarEstrategiaInicial() {
        response.then()
                .statusCode(201)
                .body(matchesJsonSchemaInClasspath("schemas/strategy.schema.json"))
                .body("vigente", equalTo(true));
        firstStrategyId = response.jsonPath().getString("id");
    }

    @Quando("crio uma nova estratégia vigente")
    public void criarNovaEstrategia() {
        response = given()
                .header("Authorization", "Bearer " + leaderToken)
                .contentType(ContentType.JSON)
                .body(payload("Reutilização de recursos", "Reuso corporativo 2026"))
                .when()
                .post("/estrategias");
    }

    @Entao("a nova estratégia passa a ser vigente")
    public void validarNovaEstrategia() {
        response.then()
                .statusCode(201)
                .body(matchesJsonSchemaInClasspath("schemas/strategy.schema.json"))
                .body("vigente", equalTo(true));
        currentStrategyId = response.jsonPath().getString("id");
    }

    @Quando("consulto todas as estratégias")
    public void consultarEstrategias() {
        response = given()
                .header("Authorization", "Bearer " + leaderToken)
                .when()
                .get("/estrategias");
    }

    @Entao("a lista de estratégias segue o contrato")
    public void validarListaEstrategias() {
        response.then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/strategy-list.schema.json"))
                .body("id", hasItem(firstStrategyId))
                .body("id", hasItem(currentStrategyId));
    }

    @Quando("consulto a estratégia vigente")
    public void consultarEstrategiaVigente() {
        response = given()
                .header("Authorization", "Bearer " + leaderToken)
                .when()
                .get("/estrategias/vigente");
    }

    @Entao("a estratégia mais recente aparece como vigente")
    public void validarEstrategiaVigente() {
        response.then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/strategy.schema.json"))
                .body("id", equalTo(currentStrategyId))
                .body("vigente", equalTo(true));
    }

    @Quando("consulto a estratégia vigente pelo identificador")
    public void consultarEstrategiaPorId() {
        response = given()
                .header("Authorization", "Bearer " + leaderToken)
                .when()
                .get("/estrategias/" + currentStrategyId);
    }

    @Entao("a consulta da estratégia retorna seus dados")
    public void validarEstrategiaPorId() {
        response.then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/strategy.schema.json"))
                .body("id", equalTo(currentStrategyId));
    }

    @Quando("atualizo os dados da estratégia vigente")
    public void atualizarEstrategia() {
        response = given()
                .header("Authorization", "Bearer " + leaderToken)
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "data", LocalDate.now().toString(),
                        "categoria", "Reutilização e eficiência",
                        "campanha", "Reuso corporativo 2026",
                        "descricao", "Descrição atualizada pela suíte BDD"
                ))
                .when()
                .put("/estrategias/" + currentStrategyId);
    }

    @Entao("a estratégia atualizada segue o contrato")
    public void validarEstrategiaAtualizada() {
        response.then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/strategy.schema.json"))
                .body("categoria", equalTo("Reutilização e eficiência"))
                .body("vigente", equalTo(true));
    }

    @Quando("consulto o histórico geral de estratégias")
    public void consultarHistoricoGeral() {
        response = given()
                .header("Authorization", "Bearer " + leaderToken)
                .when()
                .get("/estrategias/historico");
    }

    @Entao("o histórico geral contém o ciclo de vida registrado")
    public void validarHistoricoGeral() {
        response.then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/strategy-history-list.schema.json"))
                .body("operacao", hasItem("CRIADA"))
                .body("operacao", hasItem("ATUALIZADA"));
    }

    @Quando("consulto o histórico da estratégia vigente")
    public void consultarHistoricoEstrategia() {
        response = given()
                .header("Authorization", "Bearer " + leaderToken)
                .queryParam("estrategiaId", currentStrategyId)
                .when()
                .get("/estrategias/historico");
    }

    @Entao("o histórico filtrado respeita o contrato")
    public void validarHistoricoFiltrado() {
        response.then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/strategy-history-list.schema.json"))
                .body("estrategiaId", hasItem(currentStrategyId))
                .body("operacao", hasItem("ATUALIZADA"));
    }

    @Quando("excluo uma estratégia que deixou de ser vigente")
    public void excluirEstrategiaAntiga() {
        response = given()
                .header("Authorization", "Bearer " + leaderToken)
                .when()
                .delete("/estrategias/" + firstStrategyId);
    }

    @Entao("a exclusão da estratégia antiga retorna sem conteúdo")
    public void validarExclusaoEstrategiaAntiga() {
        response.then().statusCode(204);
    }

    @Quando("consulto a estratégia antiga excluída")
    public void consultarEstrategiaExcluida() {
        response = given()
                .header("Authorization", "Bearer " + leaderToken)
                .when()
                .get("/estrategias/" + firstStrategyId);
    }

    @Entao("a estratégia excluída responde com não encontrado estruturado")
    public void validarEstrategiaExcluida() {
        response.then()
                .statusCode(404)
                .body(matchesJsonSchemaInClasspath("schemas/api-error.schema.json"))
                .body("status", equalTo(404));
    }

    @Quando("tento excluir a estratégia que continua vigente")
    public void tentarExcluirEstrategiaVigente() {
        response = given()
                .header("Authorization", "Bearer " + leaderToken)
                .when()
                .delete("/estrategias/" + currentStrategyId);
    }

    @Entao("a API rejeita a exclusão da estratégia vigente por conflito")
    public void validarConflitoEstrategiaVigente() {
        response.then()
                .statusCode(409)
                .body(matchesJsonSchemaInClasspath("schemas/api-error.schema.json"))
                .body("status", equalTo(409));
    }

    private Map<String, String> payload(String categoria, String campanha) {
        return Map.of(
                "data", LocalDate.now().toString(),
                "categoria", categoria,
                "campanha", campanha,
                "descricao", "Descrição ESG criada pela suíte BDD"
        );
    }
}
