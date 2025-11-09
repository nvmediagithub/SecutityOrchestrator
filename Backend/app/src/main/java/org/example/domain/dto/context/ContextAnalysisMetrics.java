package org.example.domain.dto.context;

import lombok.Data;

/**
 * DTO representing context analysis metrics
 */
@Data
public class ContextAnalysisMetrics {
    
    private int totalElements;
    private int analyzedElements;
    private int successCount;
    private int errorCount;
    private int warningCount;
    private double successRate;
    private double averageProcessingTime;
    private String analysisDuration;
    private String completionStatus;
    
    public ContextAnalysisMetrics() {
        this.totalElements = 0;
        this.analyzedElements = 0;
        this.successCount = 0;
        this.errorCount = 0;
        this.warningCount = 0;
        this.successRate = 0.0;
        this.averageProcessingTime = 0.0;
    }
    
    public ContextAnalysisMetrics(int totalElements, int analyzedElements, int successCount, int errorCount, int warningCount) {
        this.totalElements = totalElements;
        this.analyzedElements = analyzedElements;
        this.successCount = successCount;
        this.errorCount = errorCount;
        this.warningCount = warningCount;
        this.successRate = totalElements > 0 ? (double) successCount / totalElements : 0.0;
    }
    
    public void addSuccess() {
        this.successCount++;
        this.analyzedElements++;
        this.updateSuccessRate();
    }
    
    public void addError() {
        this.errorCount++;
        this.analyzedElements++;
        this.updateSuccessRate();
    }
    
    public void addWarning() {
        this.warningCount++;
        this.analyzedElements++;
        this.updateSuccessRate();
    }
    
    private void updateSuccessRate() {
        this.successRate = this.analyzedElements > 0 ? (double) this.successCount / this.analyzedElements : 0.0;
    }
    
    public boolean isComplete() {
        return this.analyzedElements >= this.totalElements;
    }
    
    public String getProgressPercentage() {
        if (this.totalElements == 0) return "0%";
        return String.format("%.1f%%", (double) this.analyzedElements / this.totalElements * 100);
    }
}