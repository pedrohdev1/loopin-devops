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
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;

public class ReportApiSteps {
    @Value("${app.bootstrap-token}")
    private String bootstrapToken;

    private String leaderToken;
    private String projectId;
    private String strategyId;
    private Response response;

    @Dado("um projeto com resultado está preparado para consultar o dashboard")
    public void prepararDadosDashboard() {
        ApiTestFixtures.Account leader = ApiTestFixtures.createLeaderAndLogin(bootstrapToken);
        leaderToken = leader.token();
        strategyId = ApiTestFixtures.createStrategy(leaderToken, "Economia circular", "Indicadores ESG");
        String managerToken = ApiTestFixtures.createManagerAndLogin(leaderToken).token();

        Response project = given()
                .header("Authorization", "Bearer " + managerToken)
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "nome", "Projeto de reaproveitamento",
                        "descricao", "Medição de reaproveitamento de materiais.",
                        "etapa", "PILOTO",
                        "status", "EM_ANDAMENTO",
                        "investimento", 10000,
                        "prazo", LocalDate.now().plusDays(60).toString(),
                        "aumentoProdutividadePercentual", 5,
                        "progressoPercentual", 25,
                        "estrategiaId", strategyId
                ))
                .when()
                .post("/projetos");
        project.then()
                .statusCode(201)
                .body(matchesJsonSchemaInClasspath("schemas/project.schema.json"));
        projectId = project.jsonPath().getString("id");

        given()
                .header("Authorization", "Bearer " + managerToken)
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "descricao", "Resultado acumulado do piloto",
                        "data", LocalDate.now().toString(),
                        "investimentoAdicional", 500,
                        "lucroObtido", 3000,
                        "aumentoProdutividadePercentual", 7.5,
                        "progressoPercentual", 40
                ))
                .when()
                .post("/projetos/" + projectId + "/resultados")
                .then()
                .statusCode(201)
                .body(matchesJsonSchemaInClasspath("schemas/project.schema.json"));
    }

    @Quando("consulto o dashboard geral")
    public void consultarDashboardGeral() {
        response = given()
                .header("Authorization", "Bearer " + leaderToken)
                .when()
                .get("/relatorios/dashboard");
    }

    @Entao("o dashboard geral segue o contrato e resume o projeto")
    public void validarDashboardGeral() {
        response.then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/dashboard.schema.json"))
                .body("totalProjetos", equalTo(1))
                .body("porProjeto.projetoId", hasItem(projectId))
                .body("porEstrategia.estrategiaId", hasItem(strategyId));
        assertDecimal("investimentoTotal", "10500");
        assertDecimal("lucroObtidoTotal", "3000");
        assertDecimal("roiPercentual", "28.57");
    }

    private void assertDecimal(String path, String expected) {
        java.math.BigDecimal actual = new java.math.BigDecimal(response.jsonPath().getString(path));
        assertThat(actual.compareTo(new java.math.BigDecimal(expected)), equalTo(0));
    }

    @Quando("consulto o dashboard filtrado pela estratégia")
    public void consultarDashboardPorEstrategia() {
        response = given()
                .header("Authorization", "Bearer " + leaderToken)
                .queryParam("estrategiaId", strategyId)
                .when()
                .get("/relatorios/dashboard");
    }

    @Entao("o dashboard filtrado pela estratégia contém o projeto")
    public void validarDashboardPorEstrategia() {
        response.then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/dashboard.schema.json"))
                .body("totalProjetos", equalTo(1))
                .body("porEstrategia.estrategiaId", hasItem(strategyId));
    }

    @Quando("consulto o dashboard filtrado pelo projeto")
    public void consultarDashboardPorProjeto() {
        response = given()
                .header("Authorization", "Bearer " + leaderToken)
                .queryParam("projetoId", projectId)
                .when()
                .get("/relatorios/dashboard");
    }

    @Entao("o dashboard filtrado pelo projeto contém apenas esse projeto")
    public void validarDashboardPorProjeto() {
        response.then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/dashboard.schema.json"))
                .body("totalProjetos", equalTo(1))
                .body("porProjeto.projetoId", hasItem(projectId));
    }

    @Quando("consulto o dashboard com filtros de estratégia e projeto ao mesmo tempo")
    public void consultarDashboardComDoisFiltros() {
        response = given()
                .header("Authorization", "Bearer " + leaderToken)
                .queryParam("estrategiaId", strategyId)
                .queryParam("projetoId", projectId)
                .when()
                .get("/relatorios/dashboard");
    }

    @Entao("a API rejeita a combinação de filtros com erro estruturado")
    public void validarFiltrosConflitantes() {
        response.then()
                .statusCode(400)
                .body(matchesJsonSchemaInClasspath("schemas/api-error.schema.json"))
                .body("status", equalTo(400));
    }

    @Quando("consulto o dashboard para um projeto inexistente")
    public void consultarProjetoInexistente() {
        response = given()
                .header("Authorization", "Bearer " + leaderToken)
                .queryParam("projetoId", "projeto-inexistente")
                .when()
                .get("/relatorios/dashboard");
    }

    @Entao("o filtro inexistente retorna não encontrado estruturado")
    public void validarFiltroInexistente() {
        response.then()
                .statusCode(404)
                .body(matchesJsonSchemaInClasspath("schemas/api-error.schema.json"))
                .body("status", equalTo(404));
    }

    @Quando("consulto o dashboard para uma estratégia inexistente")
    public void consultarEstrategiaInexistente() {
        response = given()
                .header("Authorization", "Bearer " + leaderToken)
                .queryParam("estrategiaId", "estrategia-inexistente")
                .when()
                .get("/relatorios/dashboard");
    }

    @Entao("o filtro de estratégia inexistente retorna não encontrado estruturado")
    public void validarEstrategiaInexistente() {
        response.then()
                .statusCode(404)
                .body(matchesJsonSchemaInClasspath("schemas/api-error.schema.json"))
                .body("status", equalTo(404));
    }
}
