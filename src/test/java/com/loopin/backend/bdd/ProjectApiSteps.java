package com.loopin.backend.bdd;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;

public class ProjectApiSteps {
    @Value("${app.bootstrap-token}")
    private String bootstrapToken;

    private String managerToken;
    private String strategyId;
    private String projectId;
    private Response response;

    @Dado("um gestor e uma estratégia vigente preparados para testar projetos")
    public void prepararGestorEstrategia() {
        ApiTestFixtures.Account leader = ApiTestFixtures.createLeaderAndLogin(bootstrapToken);
        strategyId = ApiTestFixtures.createStrategy(leader.token(), "Eficiência", "Projetos circulares");
        managerToken = ApiTestFixtures.createManagerAndLogin(leader.token()).token();
    }

    @Quando("tento criar um projeto com status não suportado")
    public void criarProjetoComStatusInvalido() {
        response = given()
                .header("Authorization", "Bearer " + managerToken)
                .contentType(ContentType.JSON)
                .body(projectPayload("STATUS_INVALIDO", 0))
                .when()
                .post("/projetos");
    }

    @Entao("a criação do projeto inválido responde com erro estruturado")
    public void validarStatusProjetoInvalido() {
        response.then()
                .statusCode(400)
                .body(matchesJsonSchemaInClasspath("schemas/api-error.schema.json"))
                .body("status", equalTo(400));
    }

    @Quando("crio um projeto vinculado à estratégia vigente")
    public void criarProjeto() {
        response = given()
                .header("Authorization", "Bearer " + managerToken)
                .contentType(ContentType.JSON)
                .body(projectPayload("PLANEJADO", 0))
                .when()
                .post("/projetos");
    }

    @Entao("o projeto é criado conforme o contrato")
    public void validarProjetoCriado() {
        response.then()
                .statusCode(201)
                .body(matchesJsonSchemaInClasspath("schemas/project.schema.json"))
                .body("status", equalTo("PLANEJADO"))
                .body("estrategiaId", equalTo(strategyId));
        projectId = response.jsonPath().getString("id");
    }

    @Quando("consulto todos os projetos como gestor")
    public void consultarProjetos() {
        response = given()
                .header("Authorization", "Bearer " + managerToken)
                .when()
                .get("/projetos");
    }

    @Entao("a lista de projetos segue o contrato")
    public void validarListaProjetos() {
        response.then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/project-list.schema.json"))
                .body("id", hasItem(projectId));
    }

    @Quando("consulto o projeto pelo identificador")
    public void consultarProjetoPorId() {
        response = given()
                .header("Authorization", "Bearer " + managerToken)
                .when()
                .get("/projetos/" + projectId);
    }

    @Entao("a consulta do projeto retorna seus dados")
    public void validarProjetoPorId() {
        response.then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/project.schema.json"))
                .body("id", equalTo(projectId));
    }

    @Quando("atualizo o progresso operacional do projeto")
    public void atualizarProjeto() {
        response = given()
                .header("Authorization", "Bearer " + managerToken)
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "nome", "Otimização do ciclo de materiais",
                        "descricao", "Piloto em execução e com medição de resultados.",
                        "etapa", "PILOTO",
                        "status", "PLANEJADO",
                        "prazo", LocalDate.now().plusDays(90).toString(),
                        "aumentoProdutividadePercentual", 8.5,
                        "progressoPercentual", 55
                ))
                .when()
                .put("/projetos/" + projectId);
    }

    @Entao("a atualização preserva os vínculos e ajusta o status")
    public void validarProjetoAtualizado() {
        response.then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/project.schema.json"))
                .body("status", equalTo("EM_ANDAMENTO"))
                .body("etapa", equalTo("PILOTO"))
                .body("estrategiaId", equalTo(strategyId));
        assertDecimal("investimento", "12000");
    }

    @Quando("consulto os resultados iniciais do projeto")
    public void consultarResultadosIniciais() {
        response = given()
                .header("Authorization", "Bearer " + managerToken)
                .when()
                .get("/projetos/" + projectId + "/resultados");
    }

    @Entao("a lista inicial de resultados está vazia e contratada")
    public void validarResultadosVazios() {
        response.then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/project-result-list.schema.json"))
                .body("$", hasSize(0));
    }

    @Quando("registro um resultado incremental do projeto")
    public void registrarResultado() {
        response = given()
                .header("Authorization", "Bearer " + managerToken)
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "data", LocalDate.now().toString(),
                        "descricao", "Primeira medição do piloto",
                        "investimentoAdicional", 1500,
                        "lucroObtido", 4200,
                        "aumentoProdutividadePercentual", 8.5,
                        "progressoPercentual", 70
                ))
                .when()
                .post("/projetos/" + projectId + "/resultados");
    }

    @Entao("o projeto retorna os totais incrementados")
    public void validarResultadoIncremental() {
        response.then()
                .statusCode(201)
                .body(matchesJsonSchemaInClasspath("schemas/project.schema.json"))
                .body("id", equalTo(projectId))
                .body("resultados", hasSize(1))
                .body("progressoPercentual", equalTo(70));
        assertDecimal("investimento", "13500");
        assertDecimal("lucroObtido", "4200");
    }

    @Quando("consulto os resultados registrados do projeto")
    public void consultarResultados() {
        response = given()
                .header("Authorization", "Bearer " + managerToken)
                .when()
                .get("/projetos/" + projectId + "/resultados");
    }

    @Entao("o resultado registrado segue o contrato")
    public void validarListaResultados() {
        response.then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/project-result-list.schema.json"))
                .body("descricao", hasItem("Primeira medição do piloto"));
    }

    @Quando("excluo o projeto concluído nos testes")
    public void excluirProjeto() {
        response = given()
                .header("Authorization", "Bearer " + managerToken)
                .when()
                .delete("/projetos/" + projectId);
    }

    @Entao("a exclusão do projeto retorna sem conteúdo")
    public void validarExclusaoProjeto() {
        response.then().statusCode(204);
    }

    @Quando("consulto o projeto após a exclusão")
    public void consultarProjetoExcluido() {
        response = given()
                .header("Authorization", "Bearer " + managerToken)
                .when()
                .get("/projetos/" + projectId);
    }

    @Entao("o projeto excluído responde com não encontrado estruturado")
    public void validarProjetoExcluido() {
        response.then()
                .statusCode(404)
                .body(matchesJsonSchemaInClasspath("schemas/api-error.schema.json"))
                .body("status", equalTo(404));
    }

    private void assertDecimal(String path, String expected) {
        BigDecimal actual = new BigDecimal(response.jsonPath().getString(path));
        assertThat(actual.compareTo(new BigDecimal(expected)), equalTo(0));
    }

    private Map<String, Object> projectPayload(String status, int progresso) {
        return Map.of(
                "nome", "Otimização do ciclo de materiais",
                "descricao", "Implantar fluxo para reduzir perdas e registrar reaproveitamento.",
                "etapa", "PLANEJAMENTO",
                "status", status,
                "investimento", 12000,
                "prazo", LocalDate.now().plusDays(90).toString(),
                "aumentoProdutividadePercentual", 0,
                "progressoPercentual", progresso,
                "estrategiaId", strategyId
        );
    }
}
