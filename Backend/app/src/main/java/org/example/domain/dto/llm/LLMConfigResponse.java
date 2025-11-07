package org.example.domain.dto.llm;

import org.example.domain.entities.LLMProvider;
import org.example.domain.entities.LLMModel;
import org.example.domain.entities.PerformanceMetrics;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Response DTO for LLM configuration information
 */
public class LLMConfigResponse {
    private LLMProvider activeProvider;
    private String activeModel;
    private Map<String, LLMProviderSettings> providers;
    private Map<String, LLMModelConfig> models;
    
    public LLMConfigResponse() {}
    
    public LLMConfigResponse(LLMProvider activeProvider, String activeModel,
                           Map<String, LLMProviderSettings> providers,
                           Map<String, LLMModelConfig> models) {
        this.activeProvider = activeProvider;
        this.activeModel = activeModel;
        this.providers = providers;
        this.models = models;
    }
    
    public static LLMConfigResponse fromDomain(LLMProvider activeProvider, String activeModel,
                                             Map<String, LLMProviderSettings> providers,
                                             Map<String, LLMModel> models) {
        Map<String, LLMModelConfig> modelConfigs = models.values().stream()
            .collect(Collectors.toMap(LLMModel::getModelName, LLMModelConfig::fromDomain));
        
        return new LLMConfigResponse(activeProvider, activeModel, providers, modelConfigs);
    }
    
    // Getters and setters
    public LLMProvider getActiveProvider() { return activeProvider; }
    public void setActiveProvider(LLMProvider activeProvider) { this.activeProvider = activeProvider; }
    
    public String getActiveModel() { return activeModel; }
    public void setActiveModel(String activeModel) { this.activeModel = activeModel; }
    
    public Map<String, LLMProviderSettings> getProviders() { return providers; }
    public void setProviders(Map<String, LLMProviderSettings> providers) { this.providers = providers; }
    
    public Map<String, LLMModelConfig> getModels() { return models; }
    public void setModels(Map<String, LLMModelConfig> models) { this.models = models; }
}