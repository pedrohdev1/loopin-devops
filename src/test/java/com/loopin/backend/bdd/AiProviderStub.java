package com.loopin.backend.bdd;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public final class AiProviderStub {
    private static final String RESPONSE = "{\"choices\":[{\"message\":{\"content\":\"{\\\"pontuacao\\\":87,\\\"prioridade\\\":\\\"ALTA\\\",\\\"justificativa\\\":\\\"Impacto mensurável e viável.\\\"}\"}}]}";
    private static final HttpServer SERVER = start();

    private AiProviderStub() {}

    private static HttpServer start() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
            server.createContext("/chat/completions", exchange -> {
                byte[] request = exchange.getRequestBody().readAllBytes();
                if (!"POST".equals(exchange.getRequestMethod()) || request.length == 0) {
                    exchange.sendResponseHeaders(400, -1);
                    exchange.close();
                    return;
                }

                byte[] body = RESPONSE.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, body.length);
                try (var output = exchange.getResponseBody()) {
                    output.write(body);
                }
            });
            server.setExecutor(Runnable::run);
            server.start();
            return server;
        } catch (IOException exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    public static String baseUrl() {
        return "http://127.0.0.1:" + SERVER.getAddress().getPort();
    }

    public static void stop() {
        SERVER.stop(0);
    }
}
