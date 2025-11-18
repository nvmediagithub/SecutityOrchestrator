package org.example.features.monitoring.monitoring.infrastructure.services;

import org.example.features.monitoring.monitoring.domain.entities.Metric;
import org.example.features.monitoring.monitoring.domain.valueobjects.MetricType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Service interface for monitoring external services.
 */
public interface ExternalServiceMonitor {

    /**
     * Checks if an external service is healthy.
     *
     * @param serviceUrl The URL of the external service
     * @return CompletableFuture containing true if service is healthy
     */
    CompletableFuture<Boolean> isServiceHealthy(String serviceUrl);

    /**
     * Gets response time for an external service.
     *
     * @param serviceUrl The URL of the external service
     * @return CompletableFuture containing response time in milliseconds
     */
    CompletableFuture<Long> getServiceResponseTime(String serviceUrl);

    /**
     * Monitors LLM provider services.
     *
     * @return CompletableFuture containing list of LLM provider health metrics
     */
    CompletableFuture<List<Metric>> monitorLlmProviders();

    /**
     * Checks if external service is responding within acceptable time.
     *
     * @param serviceUrl The URL of the external service
     * @param timeoutMs Timeout in milliseconds
     * @return CompletableFuture containing true if response is within timeout
     */
    CompletableFuture<Boolean> isServiceResponsive(String serviceUrl, long timeoutMs);

    /**
     * Gets comprehensive health metrics for all monitored services.
     *
     * @return CompletableFuture containing list of all external service metrics
     */
    CompletableFuture<List<Metric>> getAllExternalServiceMetrics();

    /**
     * Records a service health check failure.
     *
     * @param serviceUrl The URL of the failed service
     * @param error Error message
     */
    void recordServiceFailure(String serviceUrl, String error);
}