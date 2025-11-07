package org.example.domain.dto.llm;

import org.example.domain.entities.LLMProvider;

/**
 * DTO for LLM provider settings
 */
public class LLMProviderSettings {
    private LLMProvider provider;
    private String apiKey; // Will be masked as "configured" if present
    private String baseUrl;
    private Integer timeout;
    private Integer maxRetries;
    
    public LLMProviderSettings() {}
    
    public LLMProviderSettings(LLMProvider provider, String apiKey, String baseUrl,
                             Integer timeout, Integer maxRetries) {
        this.provider = provider;
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
        this.timeout = timeout;
        this.maxRetries = maxRetries;
    }
    
    // Factory method to mask API keys
    public static LLMProviderSettings withMaskedKey(LLMProvider provider, boolean hasKey,
                                                   String baseUrl, Integer timeout, Integer maxRetries) {
        return new LLMProviderSettings(
            provider,
            hasKey ? "configured" : null,
            baseUrl,
            timeout,
            maxRetries
        );
    }
    
    // Getters and setters
    public LLMProvider getProvider() { return provider; }
    public void setProvider(LLMProvider provider) { this.provider = provider; }
    
    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }
    
    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
    
    public Integer getTimeout() { return timeout; }
    public void setTimeout(Integer timeout) { this.timeout = timeout; }
    
    public Integer getMaxRetries() { return maxRetries; }
    public void setMaxRetries(Integer maxRetries) { this.maxRetries = maxRetries; }
    
    public boolean hasApiKey() {
        return apiKey != null && !apiKey.trim().isEmpty();
    }
}