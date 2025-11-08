package org.example.infrastructure.llm.testdata.dto;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO for test data service statistics and monitoring
 */
public class TestDataServiceStatistics {
    
    private int activeGenerations;
    private int cachedResults;
    private long totalGenerationsCompleted;
    private long totalRecordsGenerated;
    private double averageQualityScore;
    private double averageGenerationTimeMs;
    private Map<String, Integer> providerUsage;
    private Map<String, Integer> dataTypeUsage;
    private Map<String, Integer> errorCountByType;
    private int cacheHitRate;
    private LocalDateTime serviceStartedAt;
    private LocalDateTime lastUpdated;
    
    // Constructors
    public TestDataServiceStatistics() {
        this.serviceStartedAt = LocalDateTime.now();
        this.lastUpdated = LocalDateTime.now();
        this.providerUsage = new java.util.HashMap<>();
        this.dataTypeUsage = new java.util.HashMap<>();
        this.errorCountByType = new java.util.HashMap<>();
    }
    
    // Getters and Setters
    public int getActiveGenerations() { return activeGenerations; }
    public void setActiveGenerations(int activeGenerations) { this.activeGenerations = activeGenerations; }
    
    public int getCachedResults() { return cachedResults; }
    public void setCachedResults(int cachedResults) { this.cachedResults = cachedResults; }
    
    public long getTotalGenerationsCompleted() { return totalGenerationsCompleted; }
    public void setTotalGenerationsCompleted(long totalGenerationsCompleted) { this.totalGenerationsCompleted = totalGenerationsCompleted; }
    
    public long getTotalRecordsGenerated() { return totalRecordsGenerated; }
    public void setTotalRecordsGenerated(long totalRecordsGenerated) { this.totalRecordsGenerated = totalRecordsGenerated; }
    
    public double getAverageQualityScore() { return averageQualityScore; }
    public void setAverageQualityScore(double averageQualityScore) { this.averageQualityScore = averageQualityScore; }
    
    public double getAverageGenerationTimeMs() { return averageGenerationTimeMs; }
    public void setAverageGenerationTimeMs(double averageGenerationTimeMs) { this.averageGenerationTimeMs = averageGenerationTimeMs; }
    
    public Map<String, Integer> getProviderUsage() { return providerUsage; }
    public void setProviderUsage(Map<String, Integer> providerUsage) { this.providerUsage = providerUsage; }
    
    public Map<String, Integer> getDataTypeUsage() { return dataTypeUsage; }
    public void setDataTypeUsage(Map<String, Integer> dataTypeUsage) { this.dataTypeUsage = dataTypeUsage; }
    
    public Map<String, Integer> getErrorCountByType() { return errorCountByType; }
    public void setErrorCountByType(Map<String, Integer> errorCountByType) { this.errorCountByType = errorCountByType; }
    
    public int getCacheHitRate() { return cacheHitRate; }
    public void setCacheHitRate(int cacheHitRate) { this.cacheHitRate = cacheHitRate; }
    
    public LocalDateTime getServiceStartedAt() { return serviceStartedAt; }
    public void setServiceStartedAt(LocalDateTime serviceStartedAt) { this.serviceStartedAt = serviceStartedAt; }
    
    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
    
    // Helper methods
    public void incrementProviderUsage(String provider) {
        providerUsage.put(provider, providerUsage.getOrDefault(provider, 0) + 1);
    }
    
    public void incrementDataTypeUsage(String dataType) {
        dataTypeUsage.put(dataType, dataTypeUsage.getOrDefault(dataType, 0) + 1);
    }
    
    public void incrementErrorCount(String errorType) {
        errorCountByType.put(errorType, errorCountByType.getOrDefault(errorType, 0) + 1);
    }
    
    public void markUpdated() {
        this.lastUpdated = LocalDateTime.now();
    }
    
    public String getMostUsedProvider() {
        return providerUsage.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("N/A");
    }
    
    public String getMostGeneratedDataType() {
        return dataTypeUsage.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("N/A");
    }
    
    public String getTopError() {
        return errorCountByType.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("N/A");
    }
    
    public double getServiceUptimeHours() {
        return java.time.Duration.between(serviceStartedAt, lastUpdated).toHours();
    }
    
    public double getGenerationsPerHour() {
        double hours = getServiceUptimeHours();
        return hours > 0 ? (double) totalGenerationsCompleted / hours : 0;
    }
    
    public boolean isHealthy() {
        return averageQualityScore >= 70.0 && 
               cacheHitRate >= 50.0 && 
               totalGenerationsCompleted > 0;
    }
    
    @Override
    public String toString() {
        return "TestDataServiceStatistics{" +
                "activeGenerations=" + activeGenerations +
                ", cachedResults=" + cachedResults +
                ", totalGenerationsCompleted=" + totalGenerationsCompleted +
                ", totalRecordsGenerated=" + totalRecordsGenerated +
                ", averageQualityScore=" + String.format("%.1f", averageQualityScore) +
                ", averageGenerationTimeMs=" + String.format("%.1f", averageGenerationTimeMs) +
                ", cacheHitRate=" + String.format("%.1f%%", (double) cacheHitRate) +
                ", isHealthy=" + isHealthy() +
                '}';
    }
}