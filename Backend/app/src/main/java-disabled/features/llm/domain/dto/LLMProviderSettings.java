package org.example.features.llm.domain.dto;

import java.util.Map;

/**
 * DTO for LLM provider settings
 */
public class LLMProviderSettings {

    private String apiKey;
    private String baseUrl;
    private Map<String, Object> additionalSettings;

    public LLMProviderSettings() {}

    public LLMProviderSettings(String apiKey, String baseUrl) {
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
    }

    // Getters and setters
    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }

    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }

    public Map<String, Object> getAdditionalSettings() { return additionalSettings; }
    public void setAdditionalSettings(Map<String, Object> additionalSettings) {
        this.additionalSettings = additionalSettings;
    }
}