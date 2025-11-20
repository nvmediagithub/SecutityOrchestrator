package org.example.features.analysis_processes.application.services;

import org.example.features.analysis_processes.domain.valueobjects.HttpRequestStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class HttpRequestExecutorImpl implements HttpRequestExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpRequestExecutorImpl.class);
    private final HttpClient httpClient;

    public HttpRequestExecutorImpl() {
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();
    }

    @Override
    public List<Map<String, Object>> execute(List<HttpRequestStep> steps, String baseUrl) {
        List<Map<String, Object>> results = new ArrayList<>();
        for (HttpRequestStep step : steps) {
            String url = resolveUrl(baseUrl, step.getUrl());
            HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(20))
                .method(step.getMethod().toUpperCase(), buildBody(step.getBody()));
            step.getHeaders().forEach(builder::header);

            Instant start = Instant.now();
            try {
                HttpResponse<String> response = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
                results.add(createResult(
                    step,
                    step.getStepId() != null ? step.getStepId() : step.getName(),
                    response.statusCode(),
                    response.body(),
                    Duration.between(start, Instant.now()).toMillis()
                ));
            } catch (Exception ex) {
                LOGGER.warn("HTTP request step {} failed: {}", step.getName(), ex.getMessage());
                results.add(createResult(
                    step,
                    step.getStepId() != null ? step.getStepId() : step.getName(),
                    0,
                    ex.getMessage(),
                    Duration.between(start, Instant.now()).toMillis()
                ));
            }
        }
        return results;
    }

    private HttpRequest.BodyPublisher buildBody(String body) {
        if (body == null || body.isBlank()) {
            return HttpRequest.BodyPublishers.noBody();
        }
        return HttpRequest.BodyPublishers.ofString(body);
    }

    private String resolveUrl(String baseUrl, String candidate) {
        if (!StringUtils.hasText(candidate)) {
            return baseUrl;
        }
        if (candidate.startsWith("http://") || candidate.startsWith("https://")) {
            return candidate;
        }
        if (baseUrl.endsWith("/") && candidate.startsWith("/")) {
            return baseUrl.substring(0, baseUrl.length() - 1) + candidate;
        }
        if (!baseUrl.endsWith("/") && !candidate.startsWith("/")) {
            return baseUrl + "/" + candidate;
        }
        return baseUrl + candidate;
    }

    private Map<String, Object> createResult(HttpRequestStep step, String stepId, int status, String body, long durationMs) {
        Map<String, Object> result = new HashMap<>();
        result.put("name", step.getName());
        result.put("status", status);
        result.put("body", body);
        result.put("durationMs", durationMs);
        result.put("method", step.getMethod());
        result.put("url", step.getUrl());
        result.put("stepId", stepId);
        return result;
    }
}
