package org.example.features.monitoring.monitoring.presentation.web.controllers;

import org.example.features.monitoring.monitoring.application.dto.AlertResponse;
import org.example.features.monitoring.monitoring.application.dto.LlmAnalyticsResponse;
import org.example.features.monitoring.monitoring.application.dto.LlmConnectivityResponse;
import org.example.features.monitoring.monitoring.application.dto.MetricResponse;
import org.example.features.monitoring.monitoring.application.dto.SystemHealthResponse;
import org.example.features.monitoring.monitoring.application.usecases.GetAlertsUseCase;
import org.example.features.monitoring.monitoring.application.usecases.GetLlmAnalyticsUseCase;
import org.example.features.monitoring.monitoring.application.usecases.GetMetricsUseCase;
import org.example.features.monitoring.monitoring.application.usecases.GetSystemHealthUseCase;
import org.example.features.monitoring.monitoring.application.usecases.CheckLlmConnectivityUseCase;
import org.example.features.monitoring.monitoring.application.usecases.SwitchLlmProviderUseCase;
import org.example.features.monitoring.monitoring.domain.entities.Alert;
import org.example.features.monitoring.monitoring.domain.entities.Metric;
import org.example.features.monitoring.monitoring.domain.entities.SystemHealth;
import org.example.features.monitoring.monitoring.domain.valueobjects.AlertStatus;
import org.example.features.monitoring.monitoring.domain.valueobjects.MetricType;
import org.example.shared.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * REST controller for monitoring endpoints.
 */
@RestController
@RequestMapping("/api/monitoring")
public class MonitoringController {

    private final GetSystemHealthUseCase getSystemHealthUseCase;
    private final GetMetricsUseCase getMetricsUseCase;
    private final GetAlertsUseCase getAlertsUseCase;
    private final GetLlmAnalyticsUseCase getLlmAnalyticsUseCase;
    private final SwitchLlmProviderUseCase switchLlmProviderUseCase;
    private final CheckLlmConnectivityUseCase checkLlmConnectivityUseCase;

    public MonitoringController(GetSystemHealthUseCase getSystemHealthUseCase,
                                GetMetricsUseCase getMetricsUseCase,
                                GetAlertsUseCase getAlertsUseCase,
                                GetLlmAnalyticsUseCase getLlmAnalyticsUseCase,
                                SwitchLlmProviderUseCase switchLlmProviderUseCase,
                                CheckLlmConnectivityUseCase checkLlmConnectivityUseCase) {
        this.getSystemHealthUseCase = getSystemHealthUseCase;
        this.getMetricsUseCase = getMetricsUseCase;
        this.getAlertsUseCase = getAlertsUseCase;
        this.getLlmAnalyticsUseCase = getLlmAnalyticsUseCase;
        this.switchLlmProviderUseCase = switchLlmProviderUseCase;
        this.checkLlmConnectivityUseCase = checkLlmConnectivityUseCase;
    }

    @GetMapping("/health")
    public CompletableFuture<ResponseEntity<ApiResponse<SystemHealthResponse>>> getSystemHealth() {
        return getSystemHealthUseCase.execute()
            .thenApply(this::mapToSystemHealthResponse)
            .thenApply(response -> ResponseEntity.ok(ApiResponse.success(response)));
    }

    @GetMapping("/metrics")
    public CompletableFuture<ResponseEntity<ApiResponse<List<MetricResponse>>>> getMetrics(
            @RequestParam(name = "type", required = false) MetricType type,
            @RequestParam(name = "startTime", required = false) LocalDateTime startTime,
            @RequestParam(name = "endTime", required = false) LocalDateTime endTime) {

        LocalDateTime effectiveStart = startTime != null ? startTime : LocalDateTime.now().minusHours(1);
        LocalDateTime effectiveEnd = endTime != null ? endTime : LocalDateTime.now();

        CompletableFuture<List<Metric>> metricsFuture = type != null
            ? getMetricsUseCase.execute(type, effectiveStart, effectiveEnd)
            : getMetricsUseCase.execute(effectiveStart, effectiveEnd);

        return metricsFuture
            .thenApply(metrics -> metrics.stream()
                .map(this::mapToMetricResponse)
                .collect(Collectors.toList()))
            .thenApply(responses -> ResponseEntity.ok(ApiResponse.success(responses)));
    }

    @GetMapping("/alerts")
    public CompletableFuture<ResponseEntity<ApiResponse<List<AlertResponse>>>> getAlerts(
            @RequestParam(name = "status", required = false) AlertStatus status) {
        return getAlertsUseCase.execute(status)
            .thenApply(alerts -> alerts.stream()
                .map(this::mapToAlertResponse)
                .collect(Collectors.toList()))
            .thenApply(responses -> ResponseEntity.ok(ApiResponse.success(responses)));
    }

    @GetMapping("/alerts/active")
    public CompletableFuture<ResponseEntity<ApiResponse<List<AlertResponse>>>> getActiveAlerts() {
        return getAlertsUseCase.getActiveAlerts()
            .thenApply(alerts -> alerts.stream()
                .map(this::mapToAlertResponse)
                .collect(Collectors.toList()))
            .thenApply(responses -> ResponseEntity.ok(ApiResponse.success(responses)));
    }

    @GetMapping("/llm")
    public CompletableFuture<ResponseEntity<ApiResponse<LlmAnalyticsResponse>>> getLlmAnalytics() {
        return getLlmAnalyticsUseCase.execute()
            .thenApply(response -> ResponseEntity.ok(ApiResponse.success(response)));
    }

    @PostMapping("/llm/providers/{providerId}/activate")
    public CompletableFuture<ResponseEntity<ApiResponse<LlmAnalyticsResponse>>> activateLlmProvider(
        @PathVariable("providerId") String providerId
    ) {
        return switchLlmProviderUseCase.execute(providerId)
            .thenApply(response -> ResponseEntity.ok(ApiResponse.success(response)))
            .exceptionally(ex -> {
                String message = ex.getCause() instanceof IllegalArgumentException
                    ? ex.getCause().getMessage()
                    : ex.getMessage();
                return ResponseEntity.badRequest().body(ApiResponse.error(message));
            });
    }

    @GetMapping("/llm/check")
    public CompletableFuture<ResponseEntity<ApiResponse<LlmConnectivityResponse>>> checkLlmConnectivity() {
        return checkLlmConnectivityUseCase.execute()
            .thenApply(response -> ResponseEntity.ok(ApiResponse.success(response)))
            .exceptionally(ex -> {
                String message = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
                return ResponseEntity.badRequest().body(ApiResponse.error(message));
            });
    }

    private SystemHealthResponse mapToSystemHealthResponse(SystemHealth health) {
        return new SystemHealthResponse(
            health.getId(),
            health.getStatus(),
            health.getCpuUsage(),
            health.getMemoryUsage(),
            health.getDiskUsage(),
            health.getActiveConnections(),
            health.getTimestamp(),
            health.getDetails()
        );
    }

    private MetricResponse mapToMetricResponse(Metric metric) {
        return new MetricResponse(
            metric.getId(),
            metric.getName(),
            metric.getType(),
            metric.getValue(),
            metric.getUnit(),
            metric.getTimestamp(),
            metric.getTags()
        );
    }

    private AlertResponse mapToAlertResponse(Alert alert) {
        return new AlertResponse(
            alert.getId(),
            alert.getTitle(),
            alert.getDescription(),
            alert.getSeverity(),
            alert.getStatus(),
            alert.getSource(),
            alert.getCreatedAt(),
            alert.getResolvedAt(),
            alert.getTags()
        );
    }
}
