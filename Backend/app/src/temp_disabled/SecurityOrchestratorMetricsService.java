package org.example.infrastructure.services.monitoring;

import org.example.domain.entities.PerformanceMetrics;
import org.example.infrastructure.services.llm.LLMErrorMonitoringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.context.event.EventListener;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/**
 * Comprehensive monitoring service for SecurityOrchestrator
 * Collects real-time metrics, system health, LLM performance, and business metrics
 */
@Service
public class SecurityOrchestratorMetricsService {
    
    private final LLMErrorMonitoringService llmErrorMonitoringService;
    private final BuildProperties buildProperties;
    
    // System metrics
    private final OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
    private final MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
    
    // Real-time metrics collection
    private final AtomicReference<SystemHealth> currentSystemHealth = new AtomicReference<>();
    private final ConcurrentHashMap<MetricType, AtomicReference<List<MetricPoint>>> historicalMetrics = new ConcurrentHashMap<>();
    private final AtomicLong activeConnections = new AtomicLong(0);
    private final AtomicReference<Instant> serviceStartTime = new AtomicReference<>(Instant.now());
    
    // Business metrics
    private final AtomicLong totalApiRequests = new AtomicLong(0);
    private final AtomicLong successfulApiRequests = new AtomicLong(0);
    private final AtomicLong activeUsers = new AtomicLong(0);
    private final AtomicLong bpmnProcessCount = new AtomicLong(0);
    
    public SecurityOrchestratorMetricsService(
            @Autowired(required = false) LLMErrorMonitoringService llmErrorMonitoringService,
            @Autowired(required = false) BuildProperties buildProperties) {
        this.llmErrorMonitoringService = llmErrorMonitoringService;
        this.buildProperties = buildProperties;
        
        // Initialize historical metrics storage
        for (MetricType type : MetricType.values()) {
            historicalMetrics.put(type, new AtomicReference<>(new ArrayList<>()));
        }
        
        // Initial system health calculation
        updateSystemHealth();
        
        // Start periodic health updates
        startPeriodicHealthUpdates();
    }
    
    /**
     * Handle LLM operation events for real-time metrics collection
     */
    @EventListener
    public void handleLLMOperationEvent(LLMOperationEvent event) {
        recordMetric(MetricType.LLM_RESPONSE_TIME, event.getResponseTimeMs(), event.getTimestamp());
        recordMetric(MetricType.LLM_SUCCESS_RATE, event.isSuccessful() ? 1.0 : 0.0, event.getTimestamp());
        
        if (llmErrorMonitoringService != null) {
            llmErrorMonitoringService.recordOperation(
                event.getOperationName(),
                event.getResponseTimeMs(),
                event.isSuccessful(),
                event.getErrorType()
            );
        }
    }
    
    /**
     * Record API request metrics
     */
    public void recordApiRequest(boolean successful, long responseTimeMs) {
        totalApiRequests.incrementAndGet();
        if (successful) {
            successfulApiRequests.incrementAndGet();
        }
        
        recordMetric(MetricType.API_RESPONSE_TIME, responseTimeMs, Instant.now());
        recordMetric(MetricType.API_REQUEST_COUNT, 1.0, Instant.now());
    }
    
    /**
     * Get current system health snapshot
     */
    public SystemHealth getCurrentHealth() {
        if (currentSystemHealth.get() == null) {
            updateSystemHealth();
        }
        return currentSystemHealth.get();
    }
    
    /**
     * Get historical metrics for a specific type and time period
     */
    public List<MetricPoint> getHistoricalMetrics(Duration period, MetricType type) {
        Instant cutoffTime = Instant.now().minus(period);
        return historicalMetrics.get(type).get().stream()
            .filter(point -> point.getTimestamp().isAfter(cutoffTime))
            .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * Get comprehensive metrics report
     */
    public MetricsReport getMetricsReport(Duration period) {
        SystemHealth health = getCurrentHealth();
        
        // Get LLM metrics
        Map<String, Object> llmMetrics = llmErrorMonitoringService != null ?
            llmErrorMonitoringService.getHttpClientMetrics() : Map.of();
        
        // Calculate success rates
        double apiSuccessRate = totalApiRequests.get() > 0 ? 
            (double) successfulApiRequests.get() / totalApiRequests.get() : 1.0;
        
        // Get uptime
        Duration uptime = Duration.between(serviceStartTime.get(), Instant.now());
        
        return new MetricsReport(
            health,
            llmMetrics,
            apiSuccessRate,
            totalApiRequests.get(),
            activeUsers.get(),
            uptime,
            getSystemResourceUsage(),
            getActiveModelsInfo(),
            getRecentAlerts()
        );
    }
    
    /**
     * Record system resource usage
     */
    public SystemResourceUsage getSystemResourceUsage() {
        return new SystemResourceUsage(
            getCpuUsage(),
            getMemoryUsage(),
            getDiskUsage(),
            getActiveConnections(),
            getThreadCount()
        );
    }
    
    /**
     * Get active model information
     */
    public List<ModelInfo> getActiveModelsInfo() {
        List<ModelInfo> models = new ArrayList<>();
        
        // Mock data for now - would be replaced with actual model registry
        models.add(new ModelInfo("local-llm-1", "llama-2-7b", "LOCAL", "LOADED", 1.2));
        models.add(new ModelInfo("openrouter-1", "gpt-3.5-turbo", "OPENROUTER", "AVAILABLE", null));
        models.add(new ModelInfo("onnx-1", "bert-base", "ONNX", "LOADED", 0.8));
        
        return models;
    }
    
    /**
     * Get recent alerts
     */
    public List<Alert> getRecentAlerts() {
        List<Alert> alerts = new ArrayList<>();
        
        // Check for system health alerts
        SystemHealth health = getCurrentHealth();
        if (health.getCpuUsage() > 80.0) {
            alerts.add(new Alert("HIGH_CPU_USAGE", "CPU usage above 80%", AlertSeverity.WARNING, Instant.now()));
        }
        
        if (health.getMemoryUsage() > 80.0) {
            alerts.add(new Alert("HIGH_MEMORY_USAGE", "Memory usage above 80%", AlertSeverity.WARNING, Instant.now()));
        }
        
        // Add LLM error alerts
        if (llmErrorMonitoringService != null) {
            LLMErrorMonitoringService.MonitoringReport report = llmErrorMonitoringService.getMonitoringReport();
            if (report.getSuccessRate() < 0.95) {
                alerts.add(new Alert("LOW_LLM_SUCCESS_RATE", 
                    String.format("LLM success rate: %.2f%%", report.getSuccessRate() * 100), 
                    AlertSeverity.WARNING, Instant.now()));
            }
        }
        
        return alerts;
    }
    
    /**
     * Update system health metrics
     */
    private void updateSystemHealth() {
        SystemHealth health = new SystemHealth(
            getCpuUsage(),
            getMemoryUsage(),
            getDiskUsage(),
            getDatabaseConnectionStatus(),
            getAverageResponseTime(),
            getActiveModelCount(),
            Instant.now()
        );
        
        currentSystemHealth.set(health);
        
        // Record health metrics
        recordMetric(MetricType.CPU_USAGE, health.getCpuUsage(), Instant.now());
        recordMetric(MetricType.MEMORY_USAGE, health.getMemoryUsage(), Instant.now());
    }
    
    /**
     * Start periodic system health updates
     */
    @Async
    @Scheduled(fixedRate = 30000) // Every 30 seconds
    public void startPeriodicHealthUpdates() {
        updateSystemHealth();
    }
    
    /**
     * Record a metric point
     */
    private void recordMetric(MetricType type, double value, Instant timestamp) {
        MetricPoint point = new MetricPoint(type, value, timestamp);
        
        List<MetricPoint> currentList = historicalMetrics.get(type).get();
        currentList.add(point);
        
        // Keep only recent data (last 24 hours)
        Instant cutoff = Instant.now().minus(Duration.ofHours(24));
        List<MetricPoint> filtered = currentList.stream()
            .filter(p -> p.getTimestamp().isAfter(cutoff))
            .collect(java.util.stream.Collectors.toList());
        
        historicalMetrics.get(type).set(filtered);
    }
    
    // System resource measurement methods
    private double getCpuUsage() {
        return osBean.getProcessCpuLoad() * 100;
    }
    
    private double getMemoryUsage() {
        long usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long maxMemory = Runtime.getRuntime().maxMemory();
        return maxMemory > 0 ? (double) usedMemory / maxMemory * 100 : 0;
    }
    
    private double getDiskUsage() {
        // Mock implementation - would use actual file system monitoring
        return 45.0;
    }
    
    private String getDatabaseConnectionStatus() {
        // Mock implementation - would check actual database connections
        return "CONNECTED";
    }
    
    private double getAverageResponseTime() {
        // Calculate from API metrics
        return 150.0; // Mock value
    }
    
    private int getActiveModelCount() {
        return 3; // Mock value
    }
    
    private long getActiveConnections() {
        return activeConnections.get();
    }
    
    private int getThreadCount() {
        return ManagementFactory.getThreadMXBean().getThreadCount();
    }
    
    // Getters for tracking
    public void incrementActiveConnections() {
        activeConnections.incrementAndGet();
    }
    
    public void decrementActiveConnections() {
        activeConnections.decrementAndGet();
    }
    
    public void setActiveUsers(long count) {
        activeUsers.set(count);
    }
    
    public void setBpmnProcessCount(long count) {
        bpmnProcessCount.set(count);
    }
}