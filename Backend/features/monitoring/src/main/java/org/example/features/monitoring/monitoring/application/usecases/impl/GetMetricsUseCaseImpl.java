package org.example.features.monitoring.monitoring.application.usecases.impl;

import org.example.features.monitoring.monitoring.application.usecases.GetMetricsUseCase;
import org.example.features.monitoring.monitoring.domain.entities.Metric;
import org.example.features.monitoring.monitoring.domain.valueobjects.MetricType;
import org.example.features.monitoring.monitoring.infrastructure.services.SystemMetricsService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Implementation of {@link GetMetricsUseCase} that delegates to {@link SystemMetricsService}.
 */
@Service
public class GetMetricsUseCaseImpl implements GetMetricsUseCase {

    private final SystemMetricsService systemMetricsService;

    public GetMetricsUseCaseImpl(SystemMetricsService systemMetricsService) {
        this.systemMetricsService = systemMetricsService;
    }

    @Override
    public CompletableFuture<List<Metric>> execute(LocalDateTime startTime, LocalDateTime endTime) {
        // For now we return current snapshot metrics. Historical data can be plugged in later.
        return systemMetricsService.collectAllMetrics();
    }

    @Override
    public CompletableFuture<List<Metric>> execute(MetricType type, LocalDateTime startTime, LocalDateTime endTime) {
        if (type == null) {
            return execute(startTime, endTime);
        }

        return switch (type) {
            case CPU_USAGE -> wrapSingleMetric(systemMetricsService.collectCpuUsage());
            case MEMORY_USAGE -> wrapSingleMetric(systemMetricsService.collectMemoryUsage());
            case DISK_USAGE -> wrapSingleMetric(systemMetricsService.collectDiskUsage());
            case NETWORK_USAGE -> wrapSingleMetric(systemMetricsService.collectNetworkUsage());
            case SYSTEM_LOAD -> wrapSingleMetric(systemMetricsService.getSystemLoad());
            default -> systemMetricsService.collectAllMetrics();
        };
    }

    private CompletableFuture<List<Metric>> wrapSingleMetric(CompletableFuture<Metric> metricFuture) {
        return metricFuture.thenApply(List::of);
    }
}
