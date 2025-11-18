package org.example.features.monitoring.monitoring.domain.entities;

import java.time.LocalDateTime;
import org.example.features.monitoring.monitoring.domain.valueobjects.HealthStatus;

/**
 * Domain entity representing overall system health status.
 */
public class SystemHealth {
    private final String id;
    private final HealthStatus status;
    private final double cpuUsage;
    private final double memoryUsage;
    private final double diskUsage;
    private final int activeConnections;
    private final LocalDateTime timestamp;
    private final String details;

    public SystemHealth(String id, HealthStatus status, double cpuUsage, double memoryUsage,
                       double diskUsage, int activeConnections, LocalDateTime timestamp, String details) {
        this.id = id;
        this.status = status;
        this.cpuUsage = cpuUsage;
        this.memoryUsage = memoryUsage;
        this.diskUsage = diskUsage;
        this.activeConnections = activeConnections;
        this.timestamp = timestamp;
        this.details = details;
    }

    public String getId() {
        return id;
    }

    public HealthStatus getStatus() {
        return status;
    }

    public double getCpuUsage() {
        return cpuUsage;
    }

    public double getMemoryUsage() {
        return memoryUsage;
    }

    public double getDiskUsage() {
        return diskUsage;
    }

    public int getActiveConnections() {
        return activeConnections;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getDetails() {
        return details;
    }

    public boolean isHealthy() {
        return status == HealthStatus.HEALTHY;
    }

    public boolean isDegraded() {
        return status == HealthStatus.DEGRADED;
    }

    public boolean isUnhealthy() {
        return status == HealthStatus.UNHEALTHY;
    }
}