package com.loopin.backend.controller;

import java.time.Instant;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    private final String gitCommit;

    public HealthController(@Value("${RENDER_GIT_COMMIT:local}") String gitCommit) {
        this.gitCommit = gitCommit;
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
                "status", "UP",
                "application", "loopin-backend",
                "version", gitCommit,
                "timestamp", Instant.now().toString()
        );
    }
}
