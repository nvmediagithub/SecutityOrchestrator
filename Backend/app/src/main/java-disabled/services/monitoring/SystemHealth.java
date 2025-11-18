package org.example.infrastructure.services.monitoring;

import java.time.Instant;

/**
 * System health snapshot with real-time metrics
 */
public class SystemHealth {
    private final double cpuUsage;
    private final double memoryUsage;
    private final double diskUsage;
    private final String databaseConnectionStatus;
    private final double averageResponseTime;
    private final int activeModelCount;
    private final Instant timestamp;
    
    public SystemHealth(double cpuUsage, double memoryUsage, double diskUsage,
                       String databaseConnectionStatus, double averageResponseTime,
                       int activeModelCount, Instant timestamp) {
        this.cpuUsage = cpuUsage;
        this.memoryUsage = memoryUsage;
        this.diskUsage = diskUsage;
        this.databaseConnectionStatus = databaseConnectionStatus;
        this.averageResponseTime = averageResponseTime;
        this.activeModelCount = activeModelCount;
        this.timestamp = timestamp;
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
    
    public String getDatabaseConnectionStatus() {
        return databaseConnectionStatus;
    }
    
    public double getAverageResponseTime() {
        return averageResponseTime;
    }
    
    public int getActiveModelCount() {
        return activeModelCount;
    }
    
    public Instant getTimestamp() {
        return timestamp;
    }
    
    public String getOverallStatus() {
        if (cpuUsage > 80 || memoryUsage > 80) {
            return "CRITICAL";
        } else if (cpuUsage > 60 || memoryUsage > 60) {
            return "WARNING";
        } else if (!"CONNECTED".equals(databaseConnectionStatus)) {
            return "DEGRADED";
        } else {
            return "HEALTHY";
        }
    }
    
    @Override
    public String toString() {
        return "SystemHealth{" +
                "cpuUsage=" + cpuUsage +
                "%, memoryUsage=" + memoryUsage +
                "%, diskUsage=" + diskUsage +
                "%, dbStatus='" + databaseConnectionStatus + '\'' +
                ", avgResponseTime=" + averageResponseTime +
                "ms, activeModels=" + activeModelCount +
                ", status='" + getOverallStatus() + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}