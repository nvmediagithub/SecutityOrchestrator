package org.example.features.llm.domain.dto;

import java.util.Map;

/**
 * DTO representing LLM model configuration
 */
public class LLMModelConfig {

    private String modelId;
    private String modelName;
    private String provider;
    private Map<String, Object> parameters;
    private boolean enabled;

    // Default constructor
    public LLMModelConfig() {}

    // Constructor with fields
    public LLMModelConfig(String modelId, String modelName, String provider, Map<String, Object> parameters, boolean enabled) {
        this.modelId = modelId;
        this.modelName = modelName;
        this.provider = provider;
        this.parameters = parameters;
        this.enabled = enabled;
    }

    // Getters and setters
    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}