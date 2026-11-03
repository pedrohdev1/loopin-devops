package com.loopin.backend.bdd;

import io.cucumber.java.Before;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

public class AuthenticationSteps {
    @LocalServerPort
    private int port;

    private String email;
    private final String senha = "SenhaSegura123!";
    private Response response;
    private String token;

    @Before
    public void configurarBaseUrl() {
        RestAssured.baseURI = "http://127.0.0.1";
        RestAssured.port = port;
    }

    @Quando("cadastro um novo operador")
    public void cadastroNovoOperador() {
        email = "bdd-" + UUID.randomUUID() + "@example.com";
        response = given()
                .contentType(ContentType.JSON)
                .body(Map.of("nome", "Usuário BDD", "email", email, "senha", senha))
                .when()
                .post("/usuarios");
    }

    @Entao("a API responde com usuário criado e perfil {string}")
    public void usuarioCriadoComPerfil(String perfil) {
        response.then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("id", not(equalTo("")))
                .body("email", equalTo(email))
                .body("perfil", equalTo(perfil));
    }

    @Quando("faço login com as credenciais cadastradas")
    public void loginComCredenciaisCadastradas() {
        response = given()
                .contentType(ContentType.JSON)
                .body(Map.of("email", email, "senha", senha))
                .when()
                .post("/auth/login");
    }

    @Entao("recebo um token Bearer válido no formato esperado")
    public void tokenBearerValido() {
        response.then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/login-response.schema.json"))
                .body("token", notNullValue())
                .body("token", not(equalTo("")))
                .body("tipoToken", equalTo("Bearer"));
        token = response.jsonPath().getString("token");
    }

    @Entao("consigo consultar meu próprio perfil autenticado")
    public void consultarPerfilAutenticado() {
        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/usuarios/me")
                .then()
                .statusCode(200)
                .body("email", equalTo(email))
                .body("perfil", equalTo("OPERADOR"));
    }

    @Dado("um e-mail sem usuário cadastrado")
    public void emailSemUsuarioCadastrado() {
        email = "inexistente-" + UUID.randomUUID() + "@example.com";
    }

    @Quando("tento autenticar com uma senha inválida")
    public void loginSenhaInvalida() {
        response = given()
                .contentType(ContentType.JSON)
                .body(Map.of("email", email, "senha", "SenhaErrada123!"))
                .when()
                .post("/auth/login");
    }

    @Quando("tento autenticar com um endereço de e-mail inválido")
    public void loginEmailInvalido() {
        response = given()
                .contentType(ContentType.JSON)
                .body(Map.of("email", "nao-e-um-email", "senha", senha))
                .when()
                .post("/auth/login");
    }

    @Entao("a API responde com status {int}")
    public void statusDaApi(int status) {
        response.then().statusCode(status);
    }

    @Entao("o erro de API respeita o contrato JSON")
    public void validarContratoErroApi() {
        response.then().body(matchesJsonSchemaInClasspath("schemas/api-error.schema.json"));
    }
}
