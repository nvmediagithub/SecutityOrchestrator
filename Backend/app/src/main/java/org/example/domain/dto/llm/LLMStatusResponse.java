package org.example.domain.dto.llm;

import org.example.domain.entities.LLMProvider;

import java.time.LocalDateTime;

/**
 * Response DTO for LLM provider status
 */
public class LLMStatusResponse {
    private LLMProvider provider;
    private boolean available;
    private boolean healthy;
    private Double responseTimeMs;
    private String errorMessage;
    private LocalDateTime lastCheckedAt;
    
    public LLMStatusResponse() {}
    
    public LLMStatusResponse(LLMProvider provider, boolean available, boolean healthy,
                           Double responseTimeMs, String errorMessage, LocalDateTime lastCheckedAt) {
        this.provider = provider;
        this.available = available;
        this.healthy = healthy;
        this.responseTimeMs = responseTimeMs;
        this.errorMessage = errorMessage;
        this.lastCheckedAt = lastCheckedAt;
    }
    
    // Getters and setters
    public LLMProvider getProvider() { return provider; }
    public void setProvider(LLMProvider provider) { this.provider = provider; }
    
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
    
    public boolean isHealthy() { return healthy; }
    public void setHealthy(boolean healthy) { this.healthy = healthy; }
    
    public Double getResponseTimeMs() { return responseTimeMs; }
    public void setResponseTimeMs(Double responseTimeMs) { this.responseTimeMs = responseTimeMs; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    
    public LocalDateTime getLastCheckedAt() { return lastCheckedAt; }
    public void setLastCheckedAt(LocalDateTime lastCheckedAt) { this.lastCheckedAt = lastCheckedAt; }
}