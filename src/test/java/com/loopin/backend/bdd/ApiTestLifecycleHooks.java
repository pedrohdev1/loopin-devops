package com.loopin.backend.bdd;

import io.cucumber.java.AfterAll;

public class ApiTestLifecycleHooks {
    @AfterAll
    public static void stopAiProviderStub() {
        AiProviderStub.stop();
    }
}
