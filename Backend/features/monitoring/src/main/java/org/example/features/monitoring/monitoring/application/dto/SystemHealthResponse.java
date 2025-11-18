package org.example.features.monitoring.monitoring.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.features.monitoring.monitoring.domain.valueobjects.HealthStatus;

import java.time.LocalDateTime;

/**
 * DTO for system health data response.
 */
public class SystemHealthResponse {

    @JsonProperty("id")
    private final String id;

    @JsonProperty("status")
    private final HealthStatus status;

    @JsonProperty("cpuUsage")
    private final double cpuUsage;

    @JsonProperty("memoryUsage")
    private final double memoryUsage;

    @JsonProperty("diskUsage")
    private final double diskUsage;

    @JsonProperty("activeConnections")
    private final int activeConnections;

    @JsonProperty("timestamp")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final LocalDateTime timestamp;

    @JsonProperty("details")
    private final String details;

    public SystemHealthResponse(String id, HealthStatus status, double cpuUsage, double memoryUsage,
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