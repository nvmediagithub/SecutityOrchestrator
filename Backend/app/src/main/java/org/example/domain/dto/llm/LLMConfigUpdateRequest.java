package org.example.domain.dto.llm;

import org.example.domain.entities.LLMProvider;

/**
 * Request DTO for updating LLM configuration
 */
public class LLMConfigUpdateRequest {
    private LLMProvider provider;
    private String modelName;
    private LLMProviderSettings settings;
    private LLMModelConfig llmModelConfig;
    
    public LLMConfigUpdateRequest() {}
    
    public LLMConfigUpdateRequest(LLMProvider provider, String modelName, 
                                LLMProviderSettings settings, LLMModelConfig llmModelConfig) {
        this.provider = provider;
        this.modelName = modelName;
        this.settings = settings;
        this.llmModelConfig = llmModelConfig;
    }
    
    // Getters and setters
    public LLMProvider getProvider() { return provider; }
    public void setProvider(LLMProvider provider) { this.provider = provider; }
    
    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }
    
    public LLMProviderSettings getSettings() { return settings; }
    public void setSettings(LLMProviderSettings settings) { this.settings = settings; }
    
    public LLMModelConfig getLlmModelConfig() { return llmModelConfig; }
    public void setLlmModelConfig(LLMModelConfig llmModelConfig) { this.llmModelConfig = llmModelConfig; }
}