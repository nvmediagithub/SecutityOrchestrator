package org.example.domain.model.consistency;

/**
 * Статистика проверки консистентности
 */
public class ConsistencyStatistics {
    
    private int totalApiEndpoints;
    private int totalBpmnTasks;
    private int totalChecksPerformed;
    private int totalPassedChecks;
    private int totalFailedChecks;
    private int totalWarningChecks;
    private double averageConsistencyScore;
    private double apiCoveragePercentage;
    private double bpmnCoveragePercentage;
    private int criticalIssues;
    private int mediumIssues;
    private int lowIssues;
    private int totalInconsistenciesFound;
    private long averageProcessingTimeMs;
    
    public ConsistencyStatistics() {
        this.totalApiEndpoints = 0;
        this.totalBpmnTasks = 0;
        this.totalChecksPerformed = 0;
        this.totalPassedChecks = 0;
        this.totalFailedChecks = 0;
        this.totalWarningChecks = 0;
        this.averageConsistencyScore = 85.0;
        this.apiCoveragePercentage = 0.0;
        this.bpmnCoveragePercentage = 0.0;
        this.criticalIssues = 0;
        this.mediumIssues = 0;
        this.lowIssues = 0;
        this.totalInconsistenciesFound = 0;
        this.averageProcessingTimeMs = 0;
    }
    
    // Getters and Setters
    public int getTotalApiEndpoints() { return totalApiEndpoints; }
    public void setTotalApiEndpoints(int totalApiEndpoints) { this.totalApiEndpoints = totalApiEndpoints; }
    
    public int getTotalBpmnTasks() { return totalBpmnTasks; }
    public void setTotalBpmnTasks(int totalBpmnTasks) { this.totalBpmnTasks = totalBpmnTasks; }
    
    public int getTotalChecksPerformed() { return totalChecksPerformed; }
    public void setTotalChecksPerformed(int totalChecksPerformed) { this.totalChecksPerformed = totalChecksPerformed; }
    
    public int getTotalPassedChecks() { return totalPassedChecks; }
    public void setTotalPassedChecks(int totalPassedChecks) { this.totalPassedChecks = totalPassedChecks; }
    
    public int getTotalFailedChecks() { return totalFailedChecks; }
    public void setTotalFailedChecks(int totalFailedChecks) { this.totalFailedChecks = totalFailedChecks; }
    
    public int getTotalWarningChecks() { return totalWarningChecks; }
    public void setTotalWarningChecks(int totalWarningChecks) { this.totalWarningChecks = totalWarningChecks; }
    
    public double getAverageConsistencyScore() { return averageConsistencyScore; }
    public void setAverageConsistencyScore(double averageConsistencyScore) { this.averageConsistencyScore = averageConsistencyScore; }
    
    public double getApiCoveragePercentage() { return apiCoveragePercentage; }
    public void setApiCoveragePercentage(double apiCoveragePercentage) { this.apiCoveragePercentage = apiCoveragePercentage; }
    
    public double getBpmnCoveragePercentage() { return bpmnCoveragePercentage; }
    public void setBpmnCoveragePercentage(double bpmnCoveragePercentage) { this.bpmnCoveragePercentage = bpmnCoveragePercentage; }
    
    public int getCriticalIssues() { return criticalIssues; }
    public void setCriticalIssues(int criticalIssues) { this.criticalIssues = criticalIssues; }
    
    public int getMediumIssues() { return mediumIssues; }
    public void setMediumIssues(int mediumIssues) { this.mediumIssues = mediumIssues; }
    
    public int getLowIssues() { return lowIssues; }
    public void setLowIssues(int lowIssues) { this.lowIssues = lowIssues; }
    
    public int getTotalInconsistenciesFound() { return totalInconsistenciesFound; }
    public void setTotalInconsistenciesFound(int totalInconsistenciesFound) { this.totalInconsistenciesFound = totalInconsistenciesFound; }
    
    public long getAverageProcessingTimeMs() { return averageProcessingTimeMs; }
    public void setAverageProcessingTimeMs(long averageProcessingTimeMs) { this.averageProcessingTimeMs = averageProcessingTimeMs; }
}