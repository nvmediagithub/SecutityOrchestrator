package org.example.infrastructure.llm.testdata.dto;

/**
 * Metrics for test data generation
 */
public class GenerationMetrics {
    
    private int totalRecords;
    private int successfulRecords;
    private int failedRecords;
    private double averageGenerationTimeMs;
    private long totalProcessingTimeMs;
    private int tokensUsed;
    private double cost;
    private String qualityScore;
    private int dependencyViolations;
    private int contextPreservationScore;
    
    // Constructors
    public GenerationMetrics() {}
    
    // Getters and Setters
    public int getTotalRecords() { return totalRecords; }
    public void setTotalRecords(int totalRecords) { this.totalRecords = totalRecords; }
    
    public int getSuccessfulRecords() { return successfulRecords; }
    public void setSuccessfulRecords(int successfulRecords) { this.successfulRecords = successfulRecords; }
    
    public int getFailedRecords() { return failedRecords; }
    public void setFailedRecords(int failedRecords) { this.failedRecords = failedRecords; }
    
    public double getAverageGenerationTimeMs() { return averageGenerationTimeMs; }
    public void setAverageGenerationTimeMs(double averageGenerationTimeMs) { this.averageGenerationTimeMs = averageGenerationTimeMs; }
    
    public long getTotalProcessingTimeMs() { return totalProcessingTimeMs; }
    public void setTotalProcessingTimeMs(long totalProcessingTimeMs) { this.totalProcessingTimeMs = totalProcessingTimeMs; }
    
    public int getTokensUsed() { return tokensUsed; }
    public void setTokensUsed(int tokensUsed) { this.tokensUsed = tokensUsed; }
    
    public double getCost() { return cost; }
    public void setCost(double cost) { this.cost = cost; }
    
    public String getQualityScore() { return qualityScore; }
    public void setQualityScore(String qualityScore) { this.qualityScore = qualityScore; }
    
    public int getDependencyViolations() { return dependencyViolations; }
    public void setDependencyViolations(int dependencyViolations) { this.dependencyViolations = dependencyViolations; }
    
    public int getContextPreservationScore() { return contextPreservationScore; }
    public void setContextPreservationScore(int contextPreservationScore) { this.contextPreservationScore = contextPreservationScore; }
}