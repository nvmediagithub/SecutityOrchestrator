package org.example.domain.dto.llm;

import org.example.domain.entities.LLMProvider;
import org.example.domain.entities.PerformanceMetrics;

import java.time.LocalDateTime;

/**
 * Response DTO for performance metrics report
 */
public class PerformanceReportResponse {
    private LLMProvider provider;
    private PerformanceMetrics metrics;
    private String timeRange;
    private LocalDateTime generatedAt;
    
    public PerformanceReportResponse() {}
    
    public PerformanceReportResponse(LLMProvider provider, PerformanceMetrics metrics,
                                   String timeRange, LocalDateTime generatedAt) {
        this.provider = provider;
        this.metrics = metrics;
        this.timeRange = timeRange;
        this.generatedAt = generatedAt;
    }
    
    public LLMProvider getProvider() { return provider; }
    public void setProvider(LLMProvider provider) { this.provider = provider; }
    
    public PerformanceMetrics getMetrics() { return metrics; }
    public void setMetrics(PerformanceMetrics metrics) { this.metrics = metrics; }
    
    public String getTimeRange() { return timeRange; }
    public void setTimeRange(String timeRange) { this.timeRange = timeRange; }
    
    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
}