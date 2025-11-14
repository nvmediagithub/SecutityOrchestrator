package org.example.features.llm.presentation.dto.llm;

import java.util.Map;

/**
 * DTO for LLM configuration responses
 */
public class LLMConfigResponse {

    private String currentProvider;
    private String currentModel;
    private Map<String, LLMProviderSettings> providers;
    private Map<String, LLMModelConfig> models;

    public LLMConfigResponse() {}

    public LLMConfigResponse(String currentProvider, String currentModel,
                           Map<String, LLMProviderSettings> providers,
                           Map<String, LLMModelConfig> models) {
        this.currentProvider = currentProvider;
        this.currentModel = currentModel;
        this.providers = providers;
        this.models = models;
    }

    // Getters and setters
    public String getCurrentProvider() { return currentProvider; }
    public void setCurrentProvider(String currentProvider) { this.currentProvider = currentProvider; }

    public String getCurrentModel() { return currentModel; }
    public void setCurrentModel(String currentModel) { this.currentModel = currentModel; }

    public Map<String, LLMProviderSettings> getProviders() { return providers; }
    public void setProviders(Map<String, LLMProviderSettings> providers) { this.providers = providers; }

    public Map<String, LLMModelConfig> getModels() { return models; }
    public void setModels(Map<String, LLMModelConfig> models) { this.models = models; }
}