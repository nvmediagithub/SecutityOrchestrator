package org.example.features.llm.domain.dto;

import java.util.Map;

/**
 * Response DTO for LLM configuration
 */
public class LLMConfigResponse {

    private String provider;
    private Map<String, Object> settings;
    private boolean enabled;

    // Default constructor
    public LLMConfigResponse() {}

    // Constructor with fields
    public LLMConfigResponse(String provider, Map<String, Object> settings, boolean enabled) {
        this.provider = provider;
        this.settings = settings;
        this.enabled = enabled;
    }

    // Getters and setters
    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public Map<String, Object> getSettings() {
        return settings;
    }

    public void setSettings(Map<String, Object> settings) {
        this.settings = settings;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}