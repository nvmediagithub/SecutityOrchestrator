package org.example.features.monitoring.monitoring.infrastructure.services;

import org.example.features.monitoring.monitoring.domain.entities.Metric;
import org.example.features.monitoring.monitoring.domain.valueobjects.MetricType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Service interface for collecting system metrics.
 */
public interface SystemMetricsService {

    /**
     * Collects current CPU usage metric.
     *
     * @return CompletableFuture containing CPU usage metric
     */
    CompletableFuture<Metric> collectCpuUsage();

    /**
     * Collects current memory usage metric.
     *
     * @return CompletableFuture containing memory usage metric
     */
    CompletableFuture<Metric> collectMemoryUsage();

    /**
     * Collects current disk usage metric.
     *
     * @return CompletableFuture containing disk usage metric
     */
    CompletableFuture<Metric> collectDiskUsage();

    /**
     * Collects current network usage metric.
     *
     * @return CompletableFuture containing network usage metric
     */
    CompletableFuture<Metric> collectNetworkUsage();

    /**
     * Collects all system metrics.
     *
     * @return CompletableFuture containing list of all system metrics
     */
    CompletableFuture<List<Metric>> collectAllMetrics();

    /**
     * Gets historical metrics for a specific type and time range.
     *
     * @param type Metric type
     * @param startTime Start time
     * @param endTime End time
     * @return CompletableFuture containing list of historical metrics
     */
    CompletableFuture<List<Metric>> getHistoricalMetrics(MetricType type, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * Gets the current system load average.
     *
     * @return CompletableFuture containing system load metric
     */
    CompletableFuture<Metric> getSystemLoad();
}