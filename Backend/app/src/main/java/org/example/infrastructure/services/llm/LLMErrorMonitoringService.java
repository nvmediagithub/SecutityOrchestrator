package org.example.infrastructure.services.llm;

import org.example.domain.exception.LLMException;
import org.example.infrastructure.config.HttpClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Centralized monitoring and alerting service for LLM operations
 * Tracks error rates, performance metrics, and provides alerting
 */
@Service
public class LLMErrorMonitoringService {
    
    private static final Logger logger = LoggerFactory.getLogger(LLMErrorMonitoringService.class);
    
    @Autowired
    private HttpClientConfig.HttpClientMetrics httpClientMetrics;
    
    // Error tracking
    private final ConcurrentHashMap<String, ErrorStats> errorStats = new ConcurrentHashMap<>();
    private final AtomicLong totalErrors = new AtomicLong(0);
    private final AtomicLong totalRequests = new AtomicLong(0);
    
    // Performance tracking
    private final AtomicLong totalLatency = new AtomicLong(0);
    private final AtomicReference<Instant> lastErrorTime = new AtomicReference<>();
    private final AtomicReference<Instant> serviceStartTime = new AtomicReference<>(Instant.now());
    
    // Alert thresholds
    private final AlertThresholds alertThresholds;
    
    public LLMErrorMonitoringService() {
        this(new AlertThresholds());
    }
    
    public LLMErrorMonitoringService(AlertThresholds thresholds) {
        this.alertThresholds = thresholds;
        logger.info("LLM Error Monitoring Service initialized with thresholds: {}", thresholds);
    }
    
    /**
     * Record an LLM operation
     */
    public void recordOperation(String operationName, long latencyMs, boolean successful, String errorType) {
        totalRequests.incrementAndGet();
        
        if (httpClientMetrics != null) {
            httpClientMetrics.recordRequest(latencyMs);
            if (successful) {
                httpClientMetrics.recordSuccess();
            } else {
                httpClientMetrics.recordFailure();
                if ("RATE_LIMIT".equals(errorType)) {
                    httpClientMetrics.recordRateLimitException();
                } else if ("TIMEOUT".equals(errorType)) {
                    httpClientMetrics.recordTimeoutException();
                }
            }
        }
        
        if (!successful) {
            totalErrors.incrementAndGet();
            totalLatency.addAndGet(latencyMs);
            recordError(operationName, errorType);
            
            // Check for alert conditions
            checkAlertConditions();
        } else {
            totalLatency.addAndGet(latencyMs);
        }
    }
    
    /**
     * Record error with detailed information
     */
    private void recordError(String operationName, String errorType) {
        errorStats.computeIfAbsent(operationName, k -> new ErrorStats(k))
                 .recordError(errorType);
        
        lastErrorTime.set(Instant.now());
        
        logger.warn("LLM Operation Error - Operation: {}, Error Type: {}, Time: {}", 
                   operationName, errorType, Instant.now());
    }
    
    /**
     * Record retry attempt
     */
    public void recordRetry(String operationName, int attempt, String errorType, long delayMs) {
        logger.debug("LLM Operation Retry - Operation: {}, Attempt: {}, Error: {}, Delay: {}ms", 
                    operationName, attempt, errorType, delayMs);
    }
    
    /**
     * Record circuit breaker state change
     */
    public void recordCircuitBreakerStateChange(String circuitBreakerName, 
                                               LLMCircuitBreaker.State oldState,
                                               LLMCircuitBreaker.State newState) {
        logger.warn("Circuit Breaker State Change - {}: {} -> {}", 
                   circuitBreakerName, oldState, newState);
    }
    
    /**
     * Check alert conditions
     */
    private void checkAlertConditions() {
        long errorCount = totalErrors.get();
        long totalCount = totalRequests.get();
        
        if (totalCount >= alertThresholds.minimumRequests) {
            double errorRate = (double) errorCount / totalCount;
            
            if (errorRate >= alertThresholds.errorRateThreshold) {
                triggerAlert("HIGH_ERROR_RATE", 
                           String.format("Error rate %.2f%% exceeds threshold %.2f%%", 
                                       errorRate * 100, alertThresholds.errorRateThreshold * 100));
            }
        }
        
        // Check for sustained error patterns
        Instant lastError = lastErrorTime.get();
        if (lastError != null) {
            Duration sinceLastError = Duration.between(lastError, Instant.now());
            if (sinceLastError.getSeconds() <= alertThresholds.errorSustainedSeconds) {
                if (errorStats.values().stream()
                    .anyMatch(stats -> stats.getRecentErrorCount(300) >= alertThresholds.sustainedErrorCount)) {
                    triggerAlert("SUSTAINED_ERRORS", 
                               "Sustained error pattern detected in recent operations");
                }
            }
        }
    }
    
    /**
     * Trigger alert
     */
    private void triggerAlert(String alertType, String message) {
        logger.error("LLM Service Alert [{}]: {}", alertType, message);
        
        // Here you could integrate with external alerting systems
        // e.g., Slack, PagerDuty, email, etc.
    }
    
    /**
     * Get comprehensive monitoring report
     */
    public MonitoringReport getMonitoringReport() {
        long totalReqs = totalRequests.get();
        long totalErrs = totalErrors.get();
        long totalLat = totalLatency.get();
        
        double successRate = totalReqs > 0 ? (double) (totalReqs - totalErrs) / totalReqs : 1.0;
        double avgLatency = totalReqs > 0 ? (double) totalLat / totalReqs : 0.0;
        
        return new MonitoringReport(
            serviceStartTime.get(),
            Instant.now(),
            totalReqs,
            totalErrs,
            successRate,
            avgLatency,
            new ConcurrentHashMap<>(errorStats), // Copy
            alertThresholds,
            httpClientMetrics != null ? httpClientMetrics.getMetrics() : java.util.Map.of()
        );
    }
    
    /**
     * Get HTTP client metrics
     */
    public java.util.Map<String, Object> getHttpClientMetrics() {
        return httpClientMetrics != null ? httpClientMetrics.getMetrics() : java.util.Map.of();
    }
    
    /**
     * Reset all metrics
     */
    public void resetMetrics() {
        totalErrors.set(0);
        totalRequests.set(0);
        totalLatency.set(0);
        errorStats.clear();
        lastErrorTime.set(null);
        
        if (httpClientMetrics != null) {
            httpClientMetrics.reset();
        }
        
        logger.info("LLM Error Monitoring Service metrics reset");
    }
    
    /**
     * Alert thresholds configuration
     */
    public static class AlertThresholds {
        private final double errorRateThreshold;
        private final int minimumRequests;
        private final int sustainedErrorCount;
        private final int errorSustainedSeconds;
        
        public AlertThresholds() {
            this(0.10, 100, 10, 300); // 10% error rate, min 100 requests, 10 errors in 5 minutes
        }
        
        public AlertThresholds(double errorRateThreshold, int minimumRequests, 
                             int sustainedErrorCount, int errorSustainedSeconds) {
            this.errorRateThreshold = errorRateThreshold;
            this.minimumRequests = minimumRequests;
            this.sustainedErrorCount = sustainedErrorCount;
            this.errorSustainedSeconds = errorSustainedSeconds;
        }
        
        // Getters
        public double getErrorRateThreshold() { return errorRateThreshold; }
        public int getMinimumRequests() { return minimumRequests; }
        public int getSustainedErrorCount() { return sustainedErrorCount; }
        public int getErrorSustainedSeconds() { return errorSustainedSeconds; }
        
        @Override
        public String toString() {
            return String.format("ErrorRate: %.1f%%, MinRequests: %d, SustainedErrors: %d in %ds",
                errorRateThreshold * 100, minimumRequests, sustainedErrorCount, errorSustainedSeconds);
        }
    }
    
    /**
     * Error statistics per operation
     */
    public static class ErrorStats {
        private final String operationName;
        private final AtomicLong totalErrors = new AtomicLong(0);
        private final AtomicLong recentErrors = new AtomicLong(0);
        private final ConcurrentHashMap<String, AtomicLong> errorTypeCounters = new ConcurrentHashMap<>();
        private final AtomicReference<Instant> lastErrorTime = new AtomicReference<>();
        
        public ErrorStats(String operationName) {
            this.operationName = operationName;
        }
        
        public void recordError(String errorType) {
            totalErrors.incrementAndGet();
            recentErrors.incrementAndGet();
            lastErrorTime.set(Instant.now());
            
            errorTypeCounters.computeIfAbsent(errorType, k -> new AtomicLong(0))
                           .incrementAndGet();
        }
        
        public int getRecentErrorCount(int seconds) {
            Instant lastError = lastErrorTime.get();
            if (lastError == null) return 0;
            
            Duration sinceLastError = Duration.between(lastError, Instant.now());
            return sinceLastError.getSeconds() <= seconds ? (int) recentErrors.get() : 0;
        }
        
        public long getTotalErrors() { return totalErrors.get(); }
        public long getRecentErrors() { return recentErrors.get(); }
        public String getOperationName() { return operationName; }
        public Instant getLastErrorTime() { return lastErrorTime.get(); }
        
        public java.util.Map<String, Long> getErrorTypeCounts() {
            java.util.Map<String, Long> result = new java.util.HashMap<>();
            errorTypeCounters.forEach((key, value) -> result.put(key, value.get()));
            return result;
        }
    }
    
    /**
     * Comprehensive monitoring report
     */
    public static class MonitoringReport {
        private final Instant serviceStartTime;
        private final Instant reportTime;
        private final long totalRequests;
        private final long totalErrors;
        private final double successRate;
        private final double averageLatencyMs;
        private final ConcurrentHashMap<String, ErrorStats> errorStats;
        private final AlertThresholds thresholds;
        private final java.util.Map<String, Object> httpMetrics;
        
        public MonitoringReport(Instant serviceStartTime, Instant reportTime, 
                              long totalRequests, long totalErrors, double successRate,
                              double averageLatencyMs, ConcurrentHashMap<String, ErrorStats> errorStats,
                              AlertThresholds thresholds, java.util.Map<String, Object> httpMetrics) {
            this.serviceStartTime = serviceStartTime;
            this.reportTime = reportTime;
            this.totalRequests = totalRequests;
            this.totalErrors = totalErrors;
            this.successRate = successRate;
            this.averageLatencyMs = averageLatencyMs;
            this.errorStats = errorStats;
            this.thresholds = thresholds;
            this.httpMetrics = httpMetrics;
        }
        
        // Getters
        public Instant getServiceStartTime() { return serviceStartTime; }
        public Instant getReportTime() { return reportTime; }
        public long getTotalRequests() { return totalRequests; }
        public long getTotalErrors() { return totalErrors; }
        public double getSuccessRate() { return successRate; }
        public double getAverageLatencyMs() { return averageLatencyMs; }
        public ConcurrentHashMap<String, ErrorStats> getErrorStats() { return errorStats; }
        public AlertThresholds getThresholds() { return thresholds; }
        public java.util.Map<String, Object> getHttpMetrics() { return httpMetrics; }
    }
}