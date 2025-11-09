package org.example.web.controllers;

import org.example.infrastructure.services.monitoring.SecurityOrchestratorMetricsService;
import org.example.infrastructure.services.monitoring.MetricsReport;
import org.example.infrastructure.services.monitoring.SystemHealth;
import org.example.infrastructure.services.monitoring.MetricType;
import org.example.infrastructure.services.monitoring.Alert;
import org.example.infrastructure.services.monitoring.MetricPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for monitoring dashboard API endpoints
 */
@RestController
@RequestMapping("/api/monitoring")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class MonitoringController {
    
    @Autowired
    private SecurityOrchestratorMetricsService metricsService;
    
    /**
     * Get current system health
     */
    @GetMapping("/health")
    public ResponseEntity<SystemHealth> getSystemHealth() {
        try {
            SystemHealth health = metricsService.getCurrentHealth();
            return ResponseEntity.ok(health);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Get comprehensive metrics report
     */
    @GetMapping("/metrics")
    public ResponseEntity<MetricsReport> getMetrics(
            @RequestParam(defaultValue = "1h") String period) {
        try {
            Duration duration = parseDuration(period);
            MetricsReport report = metricsService.getMetricsReport(duration);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Get historical metrics for a specific type and time period
     */
    @GetMapping("/metrics/{type}")
    public ResponseEntity<List<MetricPoint>> getHistoricalMetrics(
            @PathVariable MetricType type,
            @RequestParam(defaultValue = "1h") String period) {
        try {
            Duration duration = parseDuration(period);
            List<MetricPoint> metrics = metricsService.getHistoricalMetrics(duration, type);
            return ResponseEntity.ok(metrics);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Get active alerts
     */
    @GetMapping("/alerts")
    public ResponseEntity<List<Alert>> getActiveAlerts() {
        try {
            List<Alert> alerts = metricsService.getRecentAlerts();
            return ResponseEntity.ok(alerts);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Get system resource usage
     */
    @GetMapping("/resources")
    public ResponseEntity<org.example.infrastructure.services.monitoring.SystemResourceUsage> getSystemResources() {
        try {
            org.example.infrastructure.services.monitoring.SystemResourceUsage usage = 
                metricsService.getSystemResourceUsage();
            return ResponseEntity.ok(usage);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Get active models information
     */
    @GetMapping("/models")
    public ResponseEntity<List<org.example.infrastructure.services.monitoring.ModelInfo>> getActiveModels() {
        try {
            List<org.example.infrastructure.services.monitoring.ModelInfo> models = 
                metricsService.getActiveModelsInfo();
            return ResponseEntity.ok(models);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Record API request for monitoring
     */
    @PostMapping("/api-request")
    public ResponseEntity<Void> recordApiRequest(
            @RequestParam boolean successful,
            @RequestParam long responseTimeMs) {
        try {
            metricsService.recordApiRequest(successful, responseTimeMs);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Get monitoring dashboard summary
     */
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardSummary() {
        try {
            SystemHealth health = metricsService.getCurrentHealth();
            MetricsReport report = metricsService.getMetricsReport(Duration.ofHours(1));
            List<Alert> alerts = metricsService.getRecentAlerts();
            
            Map<String, Object> summary = Map.of(
                "systemHealth", health,
                "metricsReport", report,
                "activeAlerts", alerts,
                "timestamp", Instant.now()
            );
            
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Get real-time metrics for specific types
     */
    @PostMapping("/realtime/subscribe")
    public ResponseEntity<Map<String, Object>> subscribeToRealtimeMetrics(
            @RequestBody List<MetricType> metricTypes) {
        try {
            // Return current values for requested metric types
            Map<String, Object> realTimeData = new java.util.HashMap<>();
            
            for (MetricType type : metricTypes) {
                List<MetricPoint> recentMetrics = metricsService.getHistoricalMetrics(
                    Duration.ofMinutes(5), type);
                
                if (!recentMetrics.isEmpty()) {
                    MetricPoint latest = recentMetrics.get(recentMetrics.size() - 1);
                    realTimeData.put(type.getKey(), Map.of(
                        "value", latest.getValue(),
                        "timestamp", latest.getTimestamp()
                    ));
                }
            }
            
            return ResponseEntity.ok(realTimeData);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Health check endpoint
     */
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("Monitoring service is running");
    }
    
    /**
     * Parse duration string (e.g., "1h", "30m", "5s")
     */
    private Duration parseDuration(String durationStr) {
        if (durationStr.endsWith("h")) {
            return Duration.ofHours(Long.parseLong(durationStr.substring(0, durationStr.length() - 1)));
        } else if (durationStr.endsWith("m")) {
            return Duration.ofMinutes(Long.parseLong(durationStr.substring(0, durationStr.length() - 1)));
        } else if (durationStr.endsWith("s")) {
            return Duration.ofSeconds(Long.parseLong(durationStr.substring(0, durationStr.length() - 1)));
        }
        
        // Default to 1 hour if parsing fails
        return Duration.ofHours(1);
    }
}