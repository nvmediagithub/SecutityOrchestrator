package org.example.domain.dto.llm;

import org.example.domain.entities.LLMProvider;

import java.time.LocalDateTime;

/**
 * DTO for local model information
 */
public class LocalModelInfo {
    private String modelName;
    private Double sizeGB;
    private boolean loaded;
    private Integer contextWindow;
    private Integer maxTokens;
    private LocalDateTime lastUsed;
    
    public LocalModelInfo() {}
    
    public LocalModelInfo(String modelName, Double sizeGB, boolean loaded,
                        Integer contextWindow, Integer maxTokens, LocalDateTime lastUsed) {
        this.modelName = modelName;
        this.sizeGB = sizeGB;
        this.loaded = loaded;
        this.contextWindow = contextWindow;
        this.maxTokens = maxTokens;
        this.lastUsed = lastUsed;
    }
    
    // Getters and setters
    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }
    
    public Double getSizeGB() { return sizeGB; }
    public void setSizeGB(Double sizeGB) { this.sizeGB = sizeGB; }
    
    public boolean isLoaded() { return loaded; }
    public void setLoaded(boolean loaded) { this.loaded = loaded; }
    
    public Integer getContextWindow() { return contextWindow; }
    public void setContextWindow(Integer contextWindow) { this.contextWindow = contextWindow; }
    
    public Integer getMaxTokens() { return maxTokens; }
    public void setMaxTokens(Integer maxTokens) { this.maxTokens = maxTokens; }
    
    public LocalDateTime getLastUsed() { return lastUsed; }
    public void setLastUsed(LocalDateTime lastUsed) { this.lastUsed = lastUsed; }
}