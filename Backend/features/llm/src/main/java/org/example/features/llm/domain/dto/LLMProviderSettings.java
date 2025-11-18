package org.example.features.llm.domain.dto;

import java.util.Map;

/**
 * DTO representing LLM provider settings
 */
public class LLMProviderSettings {

    private String providerId;
    private String providerName;
    private String apiKey;
    private String baseUrl;
    private Map<String, Object> additionalSettings;
    private boolean active;

    // Default constructor
    public LLMProviderSettings() {}

    // Constructor with fields
    public LLMProviderSettings(String providerId, String providerName, String apiKey, String baseUrl, Map<String, Object> additionalSettings, boolean active) {
        this.providerId = providerId;
        this.providerName = providerName;
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
        this.additionalSettings = additionalSettings;
        this.active = active;
    }

    // Getters and setters
    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Map<String, Object> getAdditionalSettings() {
        return additionalSettings;
    }

    public void setAdditionalSettings(Map<String, Object> additionalSettings) {
        this.additionalSettings = additionalSettings;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}