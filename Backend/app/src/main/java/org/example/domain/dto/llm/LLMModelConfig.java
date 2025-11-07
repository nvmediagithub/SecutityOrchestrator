package org.example.domain.dto.llm;

import org.example.domain.entities.LLMModel;
import org.example.domain.entities.LLMProvider;
import org.example.domain.valueobjects.ModelStatus;

/**
 * DTO for LLM model configuration
 */
public class LLMModelConfig {
    private String modelName;
    private LLMProvider provider;
    private Integer contextWindow;
    private Integer maxTokens;
    private Double temperature;
    private Double topP;
    private Double frequencyPenalty;
    private Double presencePenalty;
    private Double sizeGB;
    
    public LLMModelConfig() {}
    
    public LLMModelConfig(String modelName, LLMProvider provider,
                        Integer contextWindow, Integer maxTokens, Double temperature,
                        Double topP, Double frequencyPenalty, Double presencePenalty,
                        Double sizeGB) {
        this.modelName = modelName;
        this.provider = provider;
        this.contextWindow = contextWindow;
        this.maxTokens = maxTokens;
        this.temperature = temperature;
        this.topP = topP;
        this.frequencyPenalty = frequencyPenalty;
        this.presencePenalty = presencePenalty;
        this.sizeGB = sizeGB;
    }
    
    public static LLMModelConfig fromDomain(LLMModel model) {
        return new LLMModelConfig(
            model.getModelName(),
            model.getProvider(),
            model.getContextWindow(),
            model.getMaxTokens(),
            model.getTemperature(),
            model.getTopP(),
            model.getFrequencyPenalty(),
            model.getPresencePenalty(),
            model.getSizeGB()
        );
    }
    
    // Getters and setters
    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }
    
    public LLMProvider getProvider() { return provider; }
    public void setProvider(LLMProvider provider) { this.provider = provider; }
    
    public Integer getContextWindow() { return contextWindow; }
    public void setContextWindow(Integer contextWindow) { this.contextWindow = contextWindow; }
    
    public Integer getMaxTokens() { return maxTokens; }
    public void setMaxTokens(Integer maxTokens) { this.maxTokens = maxTokens; }
    
    public Double getTemperature() { return temperature; }
    public void setTemperature(Double temperature) { this.temperature = temperature; }
    
    public Double getTopP() { return topP; }
    public void setTopP(Double topP) { this.topP = topP; }
    
    public Double getFrequencyPenalty() { return frequencyPenalty; }
    public void setFrequencyPenalty(Double frequencyPenalty) { this.frequencyPenalty = frequencyPenalty; }
    
    public Double getPresencePenalty() { return presencePenalty; }
    public void setPresencePenalty(Double presencePenalty) { this.presencePenalty = presencePenalty; }
    
    public Double getSizeGB() { return sizeGB; }
    public void setSizeGB(Double sizeGB) { this.sizeGB = sizeGB; }
}