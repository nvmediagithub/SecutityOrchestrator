package org.example.infrastructure.services.monitoring;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Comprehensive metrics report for the monitoring dashboard
 */
public class MetricsReport {
    private final SystemHealth systemHealth;
    private final Map<String, Object> llmMetrics;
    private final double apiSuccessRate;
    private final long totalApiRequests;
    private final long activeUsers;
    private final Duration uptime;
    private final SystemResourceUsage systemResourceUsage;
    private final List<ModelInfo> activeModels;
    private final List<Alert> recentAlerts;
    
    public MetricsReport(SystemHealth systemHealth, Map<String, Object> llmMetrics,
                        double apiSuccessRate, long totalApiRequests, long activeUsers,
                        Duration uptime, SystemResourceUsage systemResourceUsage,
                        List<ModelInfo> activeModels, List<Alert> recentAlerts) {
        this.systemHealth = systemHealth;
        this.llmMetrics = llmMetrics;
        this.apiSuccessRate = apiSuccessRate;
        this.totalApiRequests = totalApiRequests;
        this.activeUsers = activeUsers;
        this.uptime = uptime;
        this.systemResourceUsage = systemResourceUsage;
        this.activeModels = activeModels;
        this.recentAlerts = recentAlerts;
    }
    
    public SystemHealth getSystemHealth() {
        return systemHealth;
    }
    
    public Map<String, Object> getLlmmetrics() {
        return llmMetrics;
    }
    
    public double getApiSuccessRate() {
        return apiSuccessRate;
    }
    
    public long getTotalApiRequests() {
        return totalApiRequests;
    }
    
    public long getActiveUsers() {
        return activeUsers;
    }
    
    public Duration getUptime() {
        return uptime;
    }
    
    public SystemResourceUsage getSystemResourceUsage() {
        return systemResourceUsage;
    }
    
    public List<ModelInfo> getActiveModels() {
        return activeModels;
    }
    
    public List<Alert> getRecentAlerts() {
        return recentAlerts;
    }
    
    @Override
    public String toString() {
        return "MetricsReport{" +
                "systemHealth=" + systemHealth +
                ", apiSuccessRate=" + apiSuccessRate +
                "%, totalApiRequests=" + totalApiRequests +
                ", activeUsers=" + activeUsers +
                ", uptime=" + uptime +
                ", activeModels=" + activeModels.size() +
                ", recentAlerts=" + recentAlerts.size() +
                '}';
    }
}