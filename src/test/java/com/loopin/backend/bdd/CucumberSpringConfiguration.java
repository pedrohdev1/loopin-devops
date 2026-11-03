package com.loopin.backend.bdd;

import com.loopin.backend.LoopinBackendApplication;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@CucumberContextConfiguration
@SpringBootTest(
        classes = LoopinBackendApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "app.bootstrap-token=loopin-bdd-bootstrap-token"
)
@ActiveProfiles("test")
public class CucumberSpringConfiguration {
    @DynamicPropertySource
    static void configureApiTestIntegrations(DynamicPropertyRegistry registry) {
        registry.add("ai.openrouter.api-key", () -> "loopin-bdd-test-key");
        registry.add("ai.openrouter.base-url", AiProviderStub::baseUrl);
        registry.add("ai.openrouter.model", () -> "loopin-bdd-test-model");
    }
}
