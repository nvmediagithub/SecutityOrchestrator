package org.example.infrastructure.llm.testdata.dto;

/**
 * Service statistics for test data LLM service
 */
public class TestDataServiceStatistics {
    
    private int activeGenerations;
    private int cachedResults;
    private int totalGenerations;
    private long totalGenerationTimeMs;
    private double averageGenerationTimeMs;
    private int successfulGenerations;
    private int failedGenerations;
    private double successRate;
    private int totalTokensUsed;
    private double totalCost;
    
    // Constructors
    public TestDataServiceStatistics() {}
    
    // Getters and Setters
    public int getActiveGenerations() { return activeGenerations; }
    public void setActiveGenerations(int activeGenerations) { this.activeGenerations = activeGenerations; }
    
    public int getCachedResults() { return cachedResults; }
    public void setCachedResults(int cachedResults) { this.cachedResults = cachedResults; }
    
    public int getTotalGenerations() { return totalGenerations; }
    public void setTotalGenerations(int totalGenerations) { this.totalGenerations = totalGenerations; }
    
    public long getTotalGenerationTimeMs() { return totalGenerationTimeMs; }
    public void setTotalGenerationTimeMs(long totalGenerationTimeMs) { this.totalGenerationTimeMs = totalGenerationTimeMs; }
    
    public double getAverageGenerationTimeMs() { return averageGenerationTimeMs; }
    public void setAverageGenerationTimeMs(double averageGenerationTimeMs) { this.averageGenerationTimeMs = averageGenerationTimeMs; }
    
    public int getSuccessfulGenerations() { return successfulGenerations; }
    public void setSuccessfulGenerations(int successfulGenerations) { this.successfulGenerations = successfulGenerations; }
    
    public int getFailedGenerations() { return failedGenerations; }
    public void setFailedGenerations(int failedGenerations) { this.failedGenerations = failedGenerations; }
    
    public double getSuccessRate() { return successRate; }
    public void setSuccessRate(double successRate) { this.successRate = successRate; }
    
    public int getTotalTokensUsed() { return totalTokensUsed; }
    public void setTotalTokensUsed(int totalTokensUsed) { this.totalTokensUsed = totalTokensUsed; }
    
    public double getTotalCost() { return totalCost; }
    public void setTotalCost(double totalCost) { this.totalCost = totalCost; }
}