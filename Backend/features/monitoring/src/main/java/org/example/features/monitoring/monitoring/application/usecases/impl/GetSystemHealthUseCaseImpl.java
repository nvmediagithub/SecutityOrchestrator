package org.example.features.monitoring.monitoring.application.usecases.impl;

import org.example.features.monitoring.monitoring.application.usecases.GetSystemHealthUseCase;
import org.example.features.monitoring.monitoring.domain.entities.SystemHealth;
import org.example.features.monitoring.monitoring.domain.valueobjects.HealthStatus;
import org.example.features.monitoring.monitoring.infrastructure.services.SystemMetricsService;
import org.example.features.monitoring.monitoring.infrastructure.services.DatabaseHealthService;
import org.example.features.monitoring.monitoring.infrastructure.services.ExternalServiceMonitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

/**
 * Implementation of GetSystemHealthUseCase.
 */
@Service
public class GetSystemHealthUseCaseImpl implements GetSystemHealthUseCase {

    private final SystemMetricsService systemMetricsService;
    private final DatabaseHealthService databaseHealthService;
    private final ExternalServiceMonitor externalServiceMonitor;

    @Autowired
    public GetSystemHealthUseCaseImpl(SystemMetricsService systemMetricsService,
                                     DatabaseHealthService databaseHealthService,
                                     ExternalServiceMonitor externalServiceMonitor) {
        this.systemMetricsService = systemMetricsService;
        this.databaseHealthService = databaseHealthService;
        this.externalServiceMonitor = externalServiceMonitor;
    }

    @Override
    public CompletableFuture<SystemHealth> execute() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Check all services health
                boolean dbHealthy = databaseHealthService.isDatabaseHealthy().join();
                boolean externalHealthy = externalServiceMonitor.isServiceHealthy("https://api.openai.com").join(); // Example external service

                // Get system metrics
                double cpuUsage = systemMetricsService.collectCpuUsage().join().getValue();
                double memoryUsage = systemMetricsService.collectMemoryUsage().join().getValue();

                // Determine overall health status
                HealthStatus overallStatus = determineOverallHealth(dbHealthy, externalHealthy, cpuUsage, memoryUsage);

                return new SystemHealth(
                    "system-health-1",
                    overallStatus,
                    cpuUsage,
                    memoryUsage,
                    0.0, // Placeholder for disk usage
                    dbHealthy ? 1 : 0, // Connection count placeholder
                    LocalDateTime.now(),
                    generateHealthMessage(overallStatus)
                );
            } catch (Exception e) {
                return new SystemHealth(
                    "system-health-error",
                    HealthStatus.UNHEALTHY,
                    0.0, 0.0, 0.0, 0,
                    LocalDateTime.now(),
                    "Error checking system health: " + e.getMessage()
                );
            }
        });
    }

    private HealthStatus determineOverallHealth(boolean dbHealthy, boolean externalHealthy, double cpuUsage, double memoryUsage) {
        if (!dbHealthy || cpuUsage > 90.0 || memoryUsage > 95.0) {
            return HealthStatus.UNHEALTHY;
        } else if (!externalHealthy || cpuUsage > 70.0 || memoryUsage > 80.0) {
            return HealthStatus.DEGRADED;
        } else {
            return HealthStatus.HEALTHY;
        }
    }

    private String generateHealthMessage(HealthStatus status) {
        return switch (status) {
            case HEALTHY -> "System is operating normally";
            case DEGRADED -> "System is experiencing some issues";
            case UNHEALTHY -> "System is experiencing critical issues";
            default -> "Unknown health status";
        };
    }
}