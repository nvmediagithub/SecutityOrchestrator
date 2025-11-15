package org.example.domain.entities.analysis;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Результат анализа производительности для BPMN процессов и API
 */
public class PerformanceAnalysisResult extends AnalysisResult {
    private final List<PerformanceIssue> issues;
    private final Map<String, Duration> responseTimeByEndpoint;
    private final Map<String, Integer> throughputByProcess;
    private final Duration overallAnalysisTime;
    private final Integer bottleneckCount;
    private final String performanceGrade;
    private final List<String> recommendations;
    private final Double averageResponseTime;
    private final Integer totalRequestsPerSecond;
    private final Integer errorRatePercentage;
    
    public PerformanceAnalysisResult(Builder builder) {
        super(builder);
        this.issues = builder.issues != null ? builder.issues : new ArrayList<>();
        this.responseTimeByEndpoint = builder.responseTimeByEndpoint != null ? builder.responseTimeByEndpoint : new HashMap<>();
        this.throughputByProcess = builder.throughputByProcess != null ? builder.throughputByProcess : new HashMap<>();
        this.overallAnalysisTime = builder.overallAnalysisTime != null ? builder.overallAnalysisTime : Duration.ZERO;
        this.bottleneckCount = builder.bottleneckCount != null ? builder.bottleneckCount : 0;
        this.performanceGrade = builder.performanceGrade != null ? builder.performanceGrade : "UNKNOWN";
        this.recommendations = builder.recommendations != null ? builder.recommendations : new ArrayList<>();
        this.averageResponseTime = builder.averageResponseTime;
        this.totalRequestsPerSecond = builder.totalRequestsPerSecond;
        this.errorRatePercentage = builder.errorRatePercentage;
    }
    
    // Getters
    public List<PerformanceIssue> getIssues() {
        return issues;
    }
    
    public Map<String, Duration> getResponseTimeByEndpoint() {
        return responseTimeByEndpoint;
    }
    
    public Map<String, Integer> getThroughputByProcess() {
        return throughputByProcess;
    }
    
    public Duration getOverallAnalysisTime() {
        return overallAnalysisTime;
    }
    
    public Integer getBottleneckCount() {
        return bottleneckCount;
    }
    
    public String getPerformanceGrade() {
        return performanceGrade;
    }
    
    public List<String> getRecommendations() {
        return recommendations;
    }
    
    public Double getAverageResponseTime() {
        return averageResponseTime;
    }
    
    public Integer getTotalRequestsPerSecond() {
        return totalRequestsPerSecond;
    }
    
    public Integer getErrorRatePercentage() {
        return errorRatePercentage;
    }
    
    // Utility methods
    public boolean hasPerformanceIssues() {
        return !issues.isEmpty();
    }
    
    public Duration getAverageResponseTimeDuration() {
        if (responseTimeByEndpoint == null || responseTimeByEndpoint.isEmpty()) {
            return Duration.ZERO;
        }
        return responseTimeByEndpoint.values().stream()
            .reduce(Duration.ZERO, Duration::plus)
            .dividedBy(responseTimeByEndpoint.size());
    }
    
    public int getCriticalIssueCount() {
        return (int) issues.stream()
            .filter(issue -> "CRITICAL".equals(issue.getSeverity()))
            .count();
    }
    
    public boolean isPerformanceAcceptable() {
        return "A".equals(performanceGrade) || "B".equals(performanceGrade);
    }
    
    public boolean hasBottlenecks() {
        return bottleneckCount > 0;
    }
    
    // Inner classes
    public static class PerformanceIssue {
        private final String id;
        private final String type;
        private final String severity;
        private final String description;
        private final String affectedComponent;
        private final Duration estimatedImpact;
        private final String suggestedFix;
        
        public PerformanceIssue(String id, String type, String severity, String description,
                              String affectedComponent, Duration estimatedImpact, String suggestedFix) {
            this.id = id;
            this.type = type;
            this.severity = severity;
            this.description = description;
            this.affectedComponent = affectedComponent;
            this.estimatedImpact = estimatedImpact;
            this.suggestedFix = suggestedFix;
        }
        
        // Getters
        public String getId() { return id; }
        public String getType() { return type; }
        public String getSeverity() { return severity; }
        public String getDescription() { return description; }
        public String getAffectedComponent() { return affectedComponent; }
        public Duration getEstimatedImpact() { return estimatedImpact; }
        public String getSuggestedFix() { return suggestedFix; }
        
        public boolean isCritical() {
            return "CRITICAL".equals(severity);
        }
        
        public boolean isHighPriority() {
            return "HIGH".equals(severity) || "CRITICAL".equals(severity);
        }
    }
    
    public static class Builder extends AnalysisResult.Builder<Builder> {
        private List<PerformanceIssue> issues;
        private Map<String, Duration> responseTimeByEndpoint;
        private Map<String, Integer> throughputByProcess;
        private Duration overallAnalysisTime;
        private Integer bottleneckCount;
        private String performanceGrade;
        private List<String> recommendations;
        private Double averageResponseTime;
        private Integer totalRequestsPerSecond;
        private Integer errorRatePercentage;
        
        public Builder() {
            super();
        }
        
        @Override
        public Builder status(String status) {
            return super.status(status != null ? status : "PERFORMANCE_ANALYSIS");
        }
        
        public Builder issues(List<PerformanceIssue> issues) {
            this.issues = issues;
            return this;
        }
        
        public Builder responseTimeByEndpoint(Map<String, Duration> responseTimeByEndpoint) {
            this.responseTimeByEndpoint = responseTimeByEndpoint;
            return this;
        }
        
        public Builder throughputByProcess(Map<String, Integer> throughputByProcess) {
            this.throughputByProcess = throughputByProcess;
            return this;
        }
        
        public Builder overallAnalysisTime(Duration overallAnalysisTime) {
            this.overallAnalysisTime = overallAnalysisTime;
            return this;
        }
        
        public Builder bottleneckCount(Integer bottleneckCount) {
            this.bottleneckCount = bottleneckCount;
            return this;
        }
        
        public Builder performanceGrade(String performanceGrade) {
            this.performanceGrade = performanceGrade;
            return this;
        }
        
        public Builder recommendations(List<String> recommendations) {
            this.recommendations = recommendations;
            return this;
        }
        
        public Builder averageResponseTime(Double averageResponseTime) {
            this.averageResponseTime = averageResponseTime;
            return this;
        }
        
        public Builder totalRequestsPerSecond(Integer totalRequestsPerSecond) {
            this.totalRequestsPerSecond = totalRequestsPerSecond;
            return this;
        }
        
        public Builder errorRatePercentage(Integer errorRatePercentage) {
            this.errorRatePercentage = errorRatePercentage;
            return this;
        }
        
        @Override
        protected Builder self() {
            return this;
        }
        
        @Override
        public PerformanceAnalysisResult build() {
            return new PerformanceAnalysisResult(this);
        }
    }
}