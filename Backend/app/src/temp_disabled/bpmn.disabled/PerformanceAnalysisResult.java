package org.example.infrastructure.services.bpmn;

public class PerformanceAnalysisResult {
    private double complexityScore = 0.0;
    private int totalTasks = 0;
    private int totalGateways = 0;
    private int totalEvents = 0;
    private double estimatedExecutionTime = 0.0;
    private String performanceLevel = "UNKNOWN";
    
    public PerformanceAnalysisResult() {}
    public double getComplexityScore() { return complexityScore; }
    public void setComplexityScore(double complexityScore) { this.complexityScore = complexityScore; }
    public int getTotalTasks() { return totalTasks; }
    public void setTotalTasks(int totalTasks) { this.totalTasks = totalTasks; }
    public int getTotalGateways() { return totalGateways; }
    public void setTotalGateways(int totalGateways) { this.totalGateways = totalGateways; }
    public int getTotalEvents() { return totalEvents; }
    public void setTotalEvents(int totalEvents) { this.totalEvents = totalEvents; }
    public double getEstimatedExecutionTime() { return estimatedExecutionTime; }
    public void setEstimatedExecutionTime(double estimatedExecutionTime) { this.estimatedExecutionTime = estimatedExecutionTime; }
    public String getPerformanceLevel() { return performanceLevel; }
    public void setPerformanceLevel(String performanceLevel) { this.performanceLevel = performanceLevel; }
}
