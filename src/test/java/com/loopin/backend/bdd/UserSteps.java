package com.loopin.backend.bdd;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;

public class UserSteps {
    @Value("${app.bootstrap-token}")
    private String bootstrapToken;

    private Response response;
    private String email;
    private String userId;
    private String leaderEmail;
    private final String password = "SenhaSegura123!";
    private String leaderJwt;

    @Quando("cadastro um usuário pela rota pública pedindo perfil de líder")
    public void cadastroPublicoSolicitandoLider() {
        email = "publico-" + UUID.randomUUID() + "@example.com";
        response = given().contentType(ContentType.JSON)
                .body(Map.of("nome", "Usuário Público", "email", email, "senha", password, "perfil", "LIDER"))
                .when().post("/usuarios");
    }

    @Entao("a API mantém o perfil público como {string}")
    public void perfilPublicoPermaneceOperador(String perfil) {
        response.then()
                .statusCode(201)
                .body(matchesJsonSchemaInClasspath("schemas/user-response.schema.json"))
                .body("email", equalTo(email))
                .body("perfil", equalTo(perfil));
    }

    @Dado("que já existe um operador cadastrado")
    public void operadorJaCadastrado() {
        email = "duplicado-" + UUID.randomUUID() + "@example.com";
        given().contentType(ContentType.JSON)
                .body(Map.of("nome", "Operador existente", "email", email, "senha", password))
                .when().post("/usuarios").then().statusCode(201);
    }

    @Quando("tento cadastrar novamente o mesmo e-mail")
    public void cadastroEmailDuplicado() {
        response = given().contentType(ContentType.JSON)
                .body(Map.of("nome", "Operador duplicado", "email", email, "senha", password))
                .when().post("/usuarios");
    }

    @Entao("a API responde com conflito e erro estruturado")
    public void conflitoDeCadastro() {
        response.then()
                .statusCode(409)
                .body(matchesJsonSchemaInClasspath("schemas/api-error.schema.json"))
                .body("status", equalTo(409));
    }

    @Quando("consulto meu perfil sem token")
    public void consultarMeuPerfilSemToken() {
        response = given().when().get("/usuarios/me");
    }

    @Entao("a API bloqueia a consulta com status {int} e erro de segurança")
    public void consultarPerfilBloqueado(int status) {
        response.then()
                .statusCode(status)
                .body(matchesJsonSchemaInClasspath("schemas/security-error.schema.json"))
                .body("status", equalTo(status));
    }

    @Dado("que existe um líder autenticado")
    public void liderAutenticado() {
        leaderEmail = "lider-" + UUID.randomUUID() + "@example.com";
        given().contentType(ContentType.JSON)
                .header("X-Bootstrap-Token", bootstrapToken)
                .body(Map.of("nome", "Líder BDD", "email", leaderEmail, "senha", password))
                .when().post("/auth/bootstrap-lider")
                .then().statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/user-response.schema.json"))
                .body("perfil", equalTo("LIDER"));

        response = given().contentType(ContentType.JSON)
                .body(Map.of("email", leaderEmail, "senha", password))
                .when().post("/auth/login");
        response.then().statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/login-response.schema.json"));
        leaderJwt = response.jsonPath().getString("token");
    }

    @Quando("o líder cria um usuário administrativo com perfil {string}")
    public void liderCriaUsuarioAdmin(String perfil) {
        email = "admin-" + UUID.randomUUID() + "@example.com";
        response = given().header("Authorization", "Bearer " + leaderJwt)
                .contentType(ContentType.JSON)
                .body(Map.of("nome", "Conta Administrativa", "email", email, "senha", password, "perfil", perfil))
                .when().post("/usuarios/administracao");
    }

    @Entao("a API retorna o usuário criado sem campos sensíveis")
    public void validarUsuarioCriado() {
        response.then().statusCode(201)
                .body(matchesJsonSchemaInClasspath("schemas/user-response.schema.json"))
                .body("email", equalTo(email));
        userId = response.jsonPath().getString("id");
    }

    @Quando("o líder lista os usuários")
    public void liderListaUsuarios() {
        response = given().header("Authorization", "Bearer " + leaderJwt)
                .when().get("/usuarios");
    }

    @Entao("a lista inclui o usuário criado sem expor senhas")
    public void listaDeUsuariosSemSenhas() {
        response.then().statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/user-list.schema.json"))
                .body("id", hasItem(userId));
    }

    @Quando("o líder consulta o usuário criado")
    public void liderConsultaUsuario() {
        response = given().header("Authorization", "Bearer " + leaderJwt)
                .when().get("/usuarios/" + userId);
    }

    @Entao("a resposta corresponde ao contrato de usuário")
    public void validarRespostaUsuario() {
        response.then().statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/user-response.schema.json"))
                .body("id", equalTo(userId))
                .body("email", equalTo(email));
    }

    @Quando("o líder consulta o próprio perfil")
    public void liderConsultaProprioPerfil() {
        response = given().header("Authorization", "Bearer " + leaderJwt)
                .when().get("/usuarios/me");
    }

    @Entao("o próprio perfil segue o contrato e não contém senha")
    public void validarProprioPerfil() {
        response.then().statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/user-response.schema.json"))
                .body("email", equalTo(leaderEmail))
                .body("perfil", equalTo("LIDER"));
    }

    @Quando("o líder exclui o usuário criado")
    public void liderExcluiUsuario() {
        response = given().header("Authorization", "Bearer " + leaderJwt)
                .when().delete("/usuarios/" + userId);
    }

    @Entao("a API confirma a exclusão com status 204")
    public void confirmarExclusao() {
        response.then().statusCode(204);
        response = given().header("Authorization", "Bearer " + leaderJwt)
                .when().get("/usuarios/" + userId);
    }

    @Entao("a consulta posterior responde 404 com erro estruturado")
    public void usuarioRemovidoNaoEncontrado() {
        response.then().statusCode(404)
                .body(matchesJsonSchemaInClasspath("schemas/api-error.schema.json"))
                .body("status", equalTo(404));
    }
}
