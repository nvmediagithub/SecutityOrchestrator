package org.example.infrastructure.llm.testdata.dto;

import java.time.LocalDateTime;

/**
 * Cached test data wrapper
 */
public class CachedTestData {
    
    private TestDataGenerationResult result;
    private LocalDateTime cachedAt;
    private long accessCount;
    private LocalDateTime lastAccessed;
    
    // Constructors
    public CachedTestData() {}
    
    public CachedTestData(TestDataGenerationResult result, LocalDateTime cachedAt) {
        this.result = result;
        this.cachedAt = cachedAt;
        this.accessCount = 1;
        this.lastAccessed = LocalDateTime.now();
    }
    
    // Getters and Setters
    public TestDataGenerationResult getResult() { return result; }
    public void setResult(TestDataGenerationResult result) { this.result = result; }
    
    public LocalDateTime getCachedAt() { return cachedAt; }
    public void setCachedAt(LocalDateTime cachedAt) { this.cachedAt = cachedAt; }
    
    public long getAccessCount() { return accessCount; }
    public void setAccessCount(long accessCount) { this.accessCount = accessCount; }
    
    public LocalDateTime getLastAccessed() { return lastAccessed; }
    public void setLastAccessed(LocalDateTime lastAccessed) { this.lastAccessed = lastAccessed; }
    
    // Helper methods
    public void incrementAccess() {
        this.accessCount++;
        this.lastAccessed = LocalDateTime.now();
    }
}