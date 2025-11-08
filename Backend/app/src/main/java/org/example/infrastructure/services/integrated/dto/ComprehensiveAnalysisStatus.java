package org.example.infrastructure.services.integrated.dto;

import java.time.LocalDateTime;

/**
 * DTO для статуса комплексного анализа
 */
public class ComprehensiveAnalysisStatus {
    
    private String analysisId;
    private String status; // "STARTED", "OPENAPI_ANALYSIS", "BPMN_ANALYSIS", "INTEGRATED_ANALYSIS", "AGGREGATING", "COMPLETED", "FAILED"
    private LocalDateTime timestamp;
    private String errorMessage;
    
    // Детали прогресса
    private double overallProgress; // 0.0 - 1.0
    private int currentStep;
    private int totalSteps;
    private String currentStepDescription;
    
    // Метрики анализа
    private int apiEndpointsAnalyzed;
    private int bpmnElementsAnalyzed;
    private int contradictionsFound;
    private int issuesIdentified;
    private int recommendationsGenerated;
    
    // Время выполнения
    private LocalDateTime startTime;
    private long estimatedTimeRemainingMs;
    private long actualProcessingTimeMs;
    
    // Конструкторы
    public ComprehensiveAnalysisStatus() {}
    
    public ComprehensiveAnalysisStatus(String analysisId, String status, LocalDateTime timestamp) {
        this.analysisId = analysisId;
        this.status = status;
        this.timestamp = timestamp;
    }
    
    public ComprehensiveAnalysisStatus(String analysisId, String status, LocalDateTime timestamp, String errorMessage) {
        this(analysisId, status, timestamp);
        this.errorMessage = errorMessage;
    }
    
    // Геттеры и сеттеры
    public String getAnalysisId() { return analysisId; }
    public void setAnalysisId(String analysisId) { this.analysisId = analysisId; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    
    public double getOverallProgress() { return overallProgress; }
    public void setOverallProgress(double overallProgress) { this.overallProgress = overallProgress; }
    
    public int getCurrentStep() { return currentStep; }
    public void setCurrentStep(int currentStep) { this.currentStep = currentStep; }
    
    public int getTotalSteps() { return totalSteps; }
    public void setTotalSteps(int totalSteps) { this.totalSteps = totalSteps; }
    
    public String getCurrentStepDescription() { return currentStepDescription; }
    public void setCurrentStepDescription(String currentStepDescription) { this.currentStepDescription = currentStepDescription; }
    
    public int getApiEndpointsAnalyzed() { return apiEndpointsAnalyzed; }
    public void setApiEndpointsAnalyzed(int apiEndpointsAnalyzed) { this.apiEndpointsAnalyzed = apiEndpointsAnalyzed; }
    
    public int getBpmnElementsAnalyzed() { return bpmnElementsAnalyzed; }
    public void setBpmnElementsAnalyzed(int bpmnElementsAnalyzed) { this.bpmnElementsAnalyzed = bpmnElementsAnalyzed; }
    
    public int getContradictionsFound() { return contradictionsFound; }
    public void setContradictionsFound(int contradictionsFound) { this.contradictionsFound = contradictionsFound; }
    
    public int getIssuesIdentified() { return issuesIdentified; }
    public void setIssuesIdentified(int issuesIdentified) { this.issuesIdentified = issuesIdentified; }
    
    public int getRecommendationsGenerated() { return recommendationsGenerated; }
    public void setRecommendationsGenerated(int recommendationsGenerated) { this.recommendationsGenerated = recommendationsGenerated; }
    
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    
    public long getEstimatedTimeRemainingMs() { return estimatedTimeRemainingMs; }
    public void setEstimatedTimeRemainingMs(long estimatedTimeRemainingMs) { this.estimatedTimeRemainingMs = estimatedTimeRemainingMs; }
    
    public long getActualProcessingTimeMs() { return actualProcessingTimeMs; }
    public void setActualProcessingTimeMs(long actualProcessingTimeMs) { this.actualProcessingTimeMs = actualProcessingTimeMs; }
    
    // Вспомогательные методы
    public boolean isStarted() {
        return "STARTED".equals(status);
    }
    
    public boolean isInProgress() {
        return "OPENAPI_ANALYSIS".equals(status) ||
               "BPMN_ANALYSIS".equals(status) ||
               "INTEGRATED_ANALYSIS".equals(status) ||
               "AGGREGATING".equals(status);
    }
    
    public boolean isCompleted() {
        return "COMPLETED".equals(status);
    }
    
    public boolean hasFailed() {
        return "FAILED".equals(status);
    }
    
    public boolean hasError() {
        return errorMessage != null && !errorMessage.trim().isEmpty();
    }
    
    public double getProgressPercentage() {
        return overallProgress * 100;
    }
    
    public void updateProgress(int currentStep, int totalSteps, String description) {
        this.currentStep = currentStep;
        this.totalSteps = totalSteps;
        this.currentStepDescription = description;
        this.overallProgress = (double) currentStep / totalSteps;
        this.timestamp = LocalDateTime.now();
    }
    
    public void calculateTimeRemaining() {
        if (startTime != null && overallProgress > 0) {
            long elapsed = System.currentTimeMillis() - startTime.toInstant().toEpochMilli();
            long totalEstimated = (long) (elapsed / overallProgress);
            this.estimatedTimeRemainingMs = Math.max(0, totalEstimated - elapsed);
        }
    }
    
    public void markAsCompleted() {
        this.status = "COMPLETED";
        this.overallProgress = 1.0;
        this.currentStep = this.totalSteps;
        this.timestamp = LocalDateTime.now();
        if (startTime != null) {
            this.actualProcessingTimeMs = System.currentTimeMillis() - startTime.toInstant().toEpochMilli();
        }
    }
    
    public void markAsFailed(String error) {
        this.status = "FAILED";
        this.errorMessage = error;
        this.timestamp = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return "ComprehensiveAnalysisStatus{" +
                "analysisId='" + analysisId + '\'' +
                ", status='" + status + '\'' +
                ", progress=" + getProgressPercentage() + "%" +
                ", currentStep=" + currentStep + "/" + totalSteps +
                ", hasError=" + hasError() +
                '}';
    }
}