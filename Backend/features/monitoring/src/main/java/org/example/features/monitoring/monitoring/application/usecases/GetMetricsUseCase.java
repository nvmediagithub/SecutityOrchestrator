package org.example.features.monitoring.monitoring.application.usecases;

import org.example.features.monitoring.monitoring.domain.entities.Metric;
import org.example.features.monitoring.monitoring.domain.valueobjects.MetricType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Use case for retrieving system metrics.
 */
public interface GetMetricsUseCase {

    /**
     * Gets all metrics for a specified time range.
     *
     * @param startTime Start time for metrics retrieval
     * @param endTime End time for metrics retrieval
     * @return CompletableFuture containing list of metrics
     */
    CompletableFuture<List<Metric>> execute(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * Gets metrics of a specific type for a specified time range.
     *
     * @param type The type of metrics to retrieve
     * @param startTime Start time for metrics retrieval
     * @param endTime End time for metrics retrieval
     * @return CompletableFuture containing list of metrics
     */
    CompletableFuture<List<Metric>> execute(MetricType type, LocalDateTime startTime, LocalDateTime endTime);
}