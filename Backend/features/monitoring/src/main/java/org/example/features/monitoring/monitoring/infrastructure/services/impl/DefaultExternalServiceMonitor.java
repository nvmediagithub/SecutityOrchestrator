package org.example.features.monitoring.monitoring.infrastructure.services.impl;

import org.example.features.monitoring.monitoring.domain.entities.Metric;
import org.example.features.monitoring.monitoring.domain.valueobjects.MetricType;
import org.example.features.monitoring.monitoring.infrastructure.services.ExternalServiceMonitor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Default implementation of ExternalServiceMonitor using HTTP connections.
 */
@Service
public class DefaultExternalServiceMonitor implements ExternalServiceMonitor {

    private static final int DEFAULT_TIMEOUT_MS = 5000; // 5 seconds
    private final List<String> monitoredServices = List.of(
        "https://api.openai.com",
        "https://api.anthropic.com"
        // Add other LLM provider URLs as needed
    );

    @Override
    public CompletableFuture<Boolean> isServiceHealthy(String serviceUrl) {
        return isServiceResponsive(serviceUrl, DEFAULT_TIMEOUT_MS);
    }

    @Override
    public CompletableFuture<Long> getServiceResponseTime(String serviceUrl) {
        return CompletableFuture.supplyAsync(() -> {
            long startTime = System.currentTimeMillis();
            try {
                URL url = new URL(serviceUrl + "/health"); // Assuming health endpoint
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(DEFAULT_TIMEOUT_MS);
                connection.setReadTimeout(DEFAULT_TIMEOUT_MS);
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();
                long endTime = System.currentTimeMillis();
                return endTime - startTime;
            } catch (IOException e) {
                return -1L; // Error indicator
            }
        });
    }

    @Override
    public CompletableFuture<List<Metric>> monitorLlmProviders() {
        return CompletableFuture.supplyAsync(() -> {
            List<Metric> metrics = new ArrayList<>();
            for (String serviceUrl : monitoredServices) {
                try {
                    boolean isHealthy = isServiceHealthy(serviceUrl).get();
                    long responseTime = getServiceResponseTime(serviceUrl).get();

                    metrics.add(new Metric(
                        UUID.randomUUID().toString(),
                        "llm_provider_health",
                        MetricType.EXTERNAL_SERVICE_LATENCY,
                        isHealthy ? 1.0 : 0.0,
                        "",
                        LocalDateTime.now(),
                        "service=" + serviceUrl + ",type=health"
                    ));

                    if (responseTime >= 0) {
                        metrics.add(new Metric(
                            UUID.randomUUID().toString(),
                            "llm_provider_response_time",
                            MetricType.EXTERNAL_SERVICE_LATENCY,
                            responseTime,
                            "ms",
                            LocalDateTime.now(),
                            "service=" + serviceUrl + ",type=latency"
                        ));
                    }
                } catch (Exception e) {
                    // Record failure
                    recordServiceFailure(serviceUrl, e.getMessage());
                }
            }
            return metrics;
        });
    }

    @Override
    public CompletableFuture<Boolean> isServiceResponsive(String serviceUrl, long timeoutMs) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                URL url = new URL(serviceUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout((int) timeoutMs);
                connection.setReadTimeout((int) timeoutMs);
                connection.setRequestMethod("HEAD");

                int responseCode = connection.getResponseCode();
                return responseCode >= 200 && responseCode < 400;
            } catch (IOException e) {
                return false;
            }
        });
    }

    @Override
    public CompletableFuture<List<Metric>> getAllExternalServiceMetrics() {
        return monitorLlmProviders(); // Could be extended to include other services
    }

    @Override
    public void recordServiceFailure(String serviceUrl, String error) {
        // Note: In a real implementation, this would log to monitoring system or database
        System.err.println("Service failure for " + serviceUrl + ": " + error);
    }
}