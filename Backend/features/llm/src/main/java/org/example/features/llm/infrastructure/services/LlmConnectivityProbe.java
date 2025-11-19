package org.example.features.llm.infrastructure.services;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;

@Component
public class LlmConnectivityProbe {

    public record ProbeResult(
        boolean success,
        int statusCode,
        long latencyMs,
        String endpoint,
        String message
    ) {}

    private final HttpClient httpClient = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_1_1)
        .connectTimeout(Duration.ofSeconds(5))
        .build();

    public ProbeResult probe(LlmProviderRegistry.ProviderDescriptor descriptor) {
        URI uri = URI.create(buildProbeEndpoint(descriptor));
        HttpRequest.Builder builder = HttpRequest.newBuilder(uri)
            .timeout(Duration.ofSeconds(5))
            .GET();

        if (descriptor.requiresApiKey()) {
            builder.header("Authorization", "Bearer " + descriptor.apiKey().trim());
        }

        Instant start = Instant.now();
        try {
            HttpResponse<Void> response = httpClient.send(builder.build(), HttpResponse.BodyHandlers.discarding());
            long latency = Duration.between(start, Instant.now()).toMillis();
            boolean success = response.statusCode() >= 200 && response.statusCode() < 300;
            String message = success ? "OK" : ("HTTP " + response.statusCode());
            return new ProbeResult(success, response.statusCode(), latency, uri.toString(), message);
        } catch (IOException | InterruptedException e) {
            long latency = Duration.between(start, Instant.now()).toMillis();
            return new ProbeResult(false, 0, latency, uri.toString(), e.getMessage());
        }
    }

    private String buildProbeEndpoint(LlmProviderRegistry.ProviderDescriptor descriptor) {
        String base = descriptor.baseUrl().endsWith("/")
            ? descriptor.baseUrl().substring(0, descriptor.baseUrl().length() - 1)
            : descriptor.baseUrl();

        if ("local".equalsIgnoreCase(descriptor.mode())) {
            // Ollama exposes /api/tags for listing available models
            return base + "/api/tags";
        }
        // Remote providers typically expose /models
        return base + "/models";
    }
}
