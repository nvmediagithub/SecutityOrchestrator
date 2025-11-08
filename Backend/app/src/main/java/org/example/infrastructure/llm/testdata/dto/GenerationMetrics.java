package org.example.infrastructure.llm.testdata.dto;

import java.time.LocalDateTime;

/**
 * DTO for generation performance metrics
 */
public class GenerationMetrics {
    
    private long totalExecutionTimeMs;
    private int tokensGenerated;
    private int tokensUsed;
    private int recordsGenerated;
    private double recordsPerSecond;
    private int cacheHits;
    private int cacheMisses;
    private int validationErrors;
    private int dependencyResolutions;
    private double qualityScore;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private String provider;
    private String model;
    private double cost;
    private int retries;
    private int batchSize;
    
    // Constructors
    public GenerationMetrics() {
        this.startedAt = LocalDateTime.now();
    }
    
    public GenerationMetrics(long executionTimeMs, int recordsGenerated) {
        this();
        this.totalExecutionTimeMs = executionTimeMs;
        this.recordsGenerated = recordsGenerated;
        this.completedAt = LocalDateTime.now();
        this.recordsPerSecond = executionTimeMs > 0 ? 
            (double) recordsGenerated * 1000 / executionTimeMs : 0;
    }
    
    // Getters and Setters
    public long getTotalExecutionTimeMs() { return totalExecutionTimeMs; }
    public void setTotalExecutionTimeMs(long totalExecutionTimeMs) { this.totalExecutionTimeMs = totalExecutionTimeMs; }
    
    public int getTokensGenerated() { return tokensGenerated; }
    public void setTokensGenerated(int tokensGenerated) { this.tokensGenerated = tokensGenerated; }
    
    public int getTokensUsed() { return tokensUsed; }
    public void setTokensUsed(int tokensUsed) { this.tokensUsed = tokensUsed; }
    
    public int getRecordsGenerated() { return recordsGenerated; }
    public void setRecordsGenerated(int recordsGenerated) { this.recordsGenerated = recordsGenerated; }
    
    public double getRecordsPerSecond() { return recordsPerSecond; }
    public void setRecordsPerSecond(double recordsPerSecond) { this.recordsPerSecond = recordsPerSecond; }
    
    public int getCacheHits() { return cacheHits; }
    public void setCacheHits(int cacheHits) { this.cacheHits = cacheHits; }
    
    public int getCacheMisses() { return cacheMisses; }
    public void setCacheMisses(int cacheMisses) { this.cacheMisses = cacheMisses; }
    
    public int getValidationErrors() { return validationErrors; }
    public void setValidationErrors(int validationErrors) { this.validationErrors = validationErrors; }
    
    public int getDependencyResolutions() { return dependencyResolutions; }
    public void setDependencyResolutions(int dependencyResolutions) { this.dependencyResolutions = dependencyResolutions; }
    
    public double getQualityScore() { return qualityScore; }
    public void setQualityScore(double qualityScore) { this.qualityScore = qualityScore; }
    
    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
    
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    
    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
    
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    
    public double getCost() { return cost; }
    public void setCost(double cost) { this.cost = cost; }
    
    public int getRetries() { return retries; }
    public void setRetries(int retries) { this.retries = retries; }
    
    public int getBatchSize() { return batchSize; }
    public void setBatchSize(int batchSize) { this.batchSize = batchSize; }
    
    // Helper methods
    public double getCacheHitRate() {
        int total = cacheHits + cacheMisses;
        return total > 0 ? (double) cacheHits / total * 100 : 0;
    }
    
    public double getErrorRate() {
        int total = validationErrors + dependencyResolutions;
        return total > 0 ? (double) validationErrors / total * 100 : 0;
    }
    
    public boolean isHighPerformance() {
        return recordsPerSecond >= 100;
    }
    
    public boolean isCacheEfficient() {
        return getCacheHitRate() >= 60;
    }
    
    public boolean isLowCost() {
        return cost <= 0.10; // Less than $0.10 per generation
    }
    
    public String getPerformanceGrade() {
        if (recordsPerSecond >= 100) return "A";
        if (recordsPerSecond >= 50) return "B";
        if (recordsPerSecond >= 20) return "C";
        if (recordsPerSecond >= 10) return "D";
        return "F";
    }
    
    public void markCompleted() {
        this.completedAt = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return "GenerationMetrics{" +
                "recordsGenerated=" + recordsGenerated +
                ", recordsPerSecond=" + String.format("%.1f", recordsPerSecond) +
                ", executionTimeMs=" + totalExecutionTimeMs +
                ", qualityScore=" + String.format("%.1f", qualityScore) +
                ", performanceGrade='" + getPerformanceGrade() + '\'' +
                '}';
    }
}