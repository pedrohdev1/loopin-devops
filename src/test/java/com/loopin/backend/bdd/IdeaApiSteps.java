package com.loopin.backend.bdd;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;

public class IdeaApiSteps {
    @Value("${app.bootstrap-token}")
    private String bootstrapToken;

    private String operatorToken;
    private String managerToken;
    private String ideaId;
    private String pendingIdeaId;
    private Response response;

    @Dado("operador, gestor e estratégia vigente preparados para testar ideias")
    public void prepararAtoresIdeias() {
        ApiTestFixtures.Account leader = ApiTestFixtures.createLeaderAndLogin(bootstrapToken);
        ApiTestFixtures.createStrategy(leader.token(), "Sustentabilidade", "Ideias sustentáveis");
        operatorToken = ApiTestFixtures.createOperatorAndLogin().token();
        managerToken = ApiTestFixtures.createManagerAndLogin(leader.token()).token();
    }

    @Quando("tento cadastrar uma ideia sem título")
    public void cadastrarIdeiaInvalida() {
        response = given()
                .header("Authorization", "Bearer " + operatorToken)
                .contentType(ContentType.JSON)
                .body(Map.of("titulo", " ", "descricao", "Descrição para validação"))
                .when()
                .post("/ideias");
    }

    @Entao("a validação da ideia responde com erro estruturado")
    public void validarErroIdeia() {
        response.then()
                .statusCode(400)
                .body(matchesJsonSchemaInClasspath("schemas/api-error.schema.json"))
                .body("status", equalTo(400));
    }

    @Quando("crio uma ideia para avaliação")
    public void criarIdeia() {
        response = criarIdeia("Reduzir desperdício de materiais", "Reaproveitar materiais e medir a redução de descarte.");
    }

    @Entao("a ideia é criada conforme o contrato")
    public void validarIdeiaCriada() {
        response.then()
                .statusCode(201)
                .body(matchesJsonSchemaInClasspath("schemas/idea.schema.json"))
                .body("status", equalTo("PENDENTE"))
                .body("prioridade", equalTo("NAO_AVALIADA"));
        ideaId = response.jsonPath().getString("id");
    }

    @Quando("consulto minhas ideias")
    public void consultarMinhasIdeias() {
        response = given()
                .header("Authorization", "Bearer " + operatorToken)
                .when()
                .get("/ideias/minhas");
    }

    @Entao("minhas ideias seguem o contrato e incluem a nova ideia")
    public void validarMinhasIdeias() {
        response.then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/idea-list.schema.json"))
                .body("id", hasItem(ideaId));
    }

    @Quando("consulto a ideia pelo identificador")
    public void consultarIdeia() {
        response = given()
                .header("Authorization", "Bearer " + operatorToken)
                .when()
                .get("/ideias/" + ideaId);
    }

    @Entao("a consulta da ideia retorna seu contrato")
    public void validarConsultaIdeia() {
        response.then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/idea.schema.json"))
                .body("id", equalTo(ideaId));
    }

    @Quando("atualizo a ideia ainda pendente")
    public void atualizarIdeia() {
        response = given()
                .header("Authorization", "Bearer " + operatorToken)
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "titulo", "Reaproveitar embalagens internas",
                        "descricao", "Registrar os materiais reaproveitados e comparar o descarte mensal."
                ))
                .when()
                .put("/ideias/" + ideaId);
    }

    @Entao("a ideia atualizada preserva seu estado pendente")
    public void validarIdeiaAtualizada() {
        response.then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/idea.schema.json"))
                .body("titulo", equalTo("Reaproveitar embalagens internas"))
                .body("status", equalTo("PENDENTE"));
    }

    @Quando("o gestor lista ideias para avaliação")
    public void gestorListaIdeias() {
        response = given()
                .header("Authorization", "Bearer " + managerToken)
                .when()
                .get("/ideias");
    }

    @Entao("a lista de gestão contém a ideia")
    public void validarListaGestaoIdeias() {
        response.then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/idea-list.schema.json"))
                .body("id", hasItem(ideaId));
    }

    @Quando("o gestor envia uma avaliação com status inválido")
    public void avaliarIdeiaComStatusInvalido() {
        response = avaliar("STATUS_INVALIDO", "ALTA");
    }

    @Entao("a avaliação inválida responde com erro estruturado")
    public void validarAvaliacaoInvalida() {
        response.then()
                .statusCode(400)
                .body(matchesJsonSchemaInClasspath("schemas/api-error.schema.json"))
                .body("status", equalTo(400));
    }

    @Quando("o gestor aprova a ideia com prioridade alta")
    public void gestorAprovaIdeia() {
        response = given()
                .header("Authorization", "Bearer " + managerToken)
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "status", "APROVADA",
                        "prioridade", "ALTA",
                        "comentarioGestor", "Impacto e viabilidade avaliados."
                ))
                .when()
                .patch("/ideias/" + ideaId + "/avaliacao");
    }

    @Entao("a avaliação atualizada segue o contrato")
    public void validarAvaliacaoIdeia() {
        response.then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/idea.schema.json"))
                .body("status", equalTo("APROVADA"))
                .body("prioridade", equalTo("ALTA"))
                .body("comentarioGestor", equalTo("Impacto e viabilidade avaliados."));
    }

    @Quando("o gestor solicita pontuação auxiliar da ideia")
    public void solicitarPontuacaoIa() {
        response = given()
                .header("Authorization", "Bearer " + managerToken)
                .when()
                .post("/ia/ideias/" + ideaId + "/pontuar");
    }

    @Entao("a recomendação de IA respeita o contrato JSON")
    public void validarPontuacaoIa() {
        response.then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/ai-score.schema.json"))
                .body("ideiaId", equalTo(ideaId))
                .body("pontuacao", equalTo(87))
                .body("prioridadeSugerida", equalTo("ALTA"))
                .body("modelo", equalTo("loopin-bdd-test-model"));
    }

    @Quando("crio uma segunda ideia pendente")
    public void criarIdeiaPendente() {
        response = criarIdeia("Separar resíduos recicláveis", "Criar ponto de coleta identificado para materiais recicláveis.");
    }

    @Entao("a segunda ideia é criada")
    public void validarSegundaIdeia() {
        response.then()
                .statusCode(201)
                .body(matchesJsonSchemaInClasspath("schemas/idea.schema.json"))
                .body("status", equalTo("PENDENTE"));
        pendingIdeaId = response.jsonPath().getString("id");
    }

    @Quando("excluo a ideia que continua pendente")
    public void excluirIdeiaPendente() {
        response = given()
                .header("Authorization", "Bearer " + operatorToken)
                .when()
                .delete("/ideias/" + pendingIdeaId);
    }

    @Entao("a exclusão da ideia retorna sem conteúdo")
    public void validarExclusaoIdeia() {
        response.then().statusCode(204);
    }

    @Quando("consulto a segunda ideia após excluí-la")
    public void consultarIdeiaExcluida() {
        response = given()
                .header("Authorization", "Bearer " + operatorToken)
                .when()
                .get("/ideias/" + pendingIdeaId);
    }

    @Entao("a ideia excluída responde com não encontrado estruturado")
    public void validarIdeiaExcluida() {
        response.then()
                .statusCode(404)
                .body(matchesJsonSchemaInClasspath("schemas/api-error.schema.json"))
                .body("status", equalTo(404));
    }

    private Response criarIdeia(String titulo, String descricao) {
        return given()
                .header("Authorization", "Bearer " + operatorToken)
                .contentType(ContentType.JSON)
                .body(Map.of("titulo", titulo, "descricao", descricao))
                .when()
                .post("/ideias");
    }

    private Response avaliar(String status, String prioridade) {
        return given()
                .header("Authorization", "Bearer " + managerToken)
                .contentType(ContentType.JSON)
                .body(Map.of("status", status, "prioridade", prioridade))
                .when()
                .patch("/ideias/" + ideaId + "/avaliacao");
    }
}
