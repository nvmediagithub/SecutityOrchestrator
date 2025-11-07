package org.example.domain.entities;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Performance metrics for LLM providers
 */
public class PerformanceMetrics {
    private final LLMProvider provider;
    private final Integer totalRequests;
    private final Integer successfulRequests;
    private final Integer failedRequests;
    private final Double averageResponseTimeMs;
    private final Integer totalTokensUsed;
    private final Double errorRate;
    private final Double uptimePercentage;
    private final LocalDateTime lastUpdated;

    public PerformanceMetrics(LLMProvider provider, Integer totalRequests,
                            Integer successfulRequests, Integer failedRequests,
                            Double averageResponseTimeMs, Integer totalTokensUsed,
                            Double errorRate, Double uptimePercentage) {
        this.provider = Objects.requireNonNull(provider, "Provider cannot be null");
        this.totalRequests = totalRequests != null ? totalRequests : 0;
        this.successfulRequests = successfulRequests != null ? successfulRequests : 0;
        this.failedRequests = failedRequests != null ? failedRequests : 0;
        this.averageResponseTimeMs = averageResponseTimeMs;
        this.totalTokensUsed = totalTokensUsed != null ? totalTokensUsed : 0;
        this.errorRate = errorRate != null ? errorRate : 0.0;
        this.uptimePercentage = uptimePercentage != null ? uptimePercentage : 100.0;
        this.lastUpdated = LocalDateTime.now();
    }

    public static PerformanceMetrics createEmpty(LLMProvider provider) {
        return new PerformanceMetrics(provider, 0, 0, 0, 0.0, 0, 0.0, 100.0);
    }

    public PerformanceMetrics updateWithNewRequest(boolean successful, Double responseTimeMs, Integer tokensUsed) {
        Integer newTotalRequests = totalRequests + 1;
        Integer newSuccessfulRequests = successful ? successfulRequests + 1 : successfulRequests;
        Integer newFailedRequests = successful ? failedRequests : failedRequests + 1;
        
        Double newErrorRate = newTotalRequests > 0 ? 
            (double) newFailedRequests / newTotalRequests * 100 : 0.0;
        
        Double newAverageResponseTimeMs = averageResponseTimeMs != null && totalRequests > 0 ?
            (averageResponseTimeMs * totalRequests + responseTimeMs) / newTotalRequests : 
            responseTimeMs;
            
        return new PerformanceMetrics(
            provider, newTotalRequests, newSuccessfulRequests, newFailedRequests,
            newAverageResponseTimeMs, totalTokensUsed + (tokensUsed != null ? tokensUsed : 0),
            newErrorRate, uptimePercentage
        );
    }

    // Getters
    public LLMProvider getProvider() { return provider; }
    public Integer getTotalRequests() { return totalRequests; }
    public Integer getSuccessfulRequests() { return successfulRequests; }
    public Integer getFailedRequests() { return failedRequests; }
    public Double getAverageResponseTimeMs() { return averageResponseTimeMs; }
    public Integer getTotalTokensUsed() { return totalTokensUsed; }
    public Double getErrorRate() { return errorRate; }
    public Double getUptimePercentage() { return uptimePercentage; }
    public LocalDateTime getLastUpdated() { return lastUpdated; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PerformanceMetrics that = (PerformanceMetrics) o;
        return provider == that.provider;
    }

    @Override
    public int hashCode() {
        return Objects.hash(provider);
    }

    @Override
    public String toString() {
        return "PerformanceMetrics{" +
                "provider=" + provider +
                ", totalRequests=" + totalRequests +
                ", successRate=" + (totalRequests > 0 ? (double) successfulRequests / totalRequests * 100 : 0) + "%" +
                ", errorRate=" + errorRate + "%" +
                '}';
    }
}