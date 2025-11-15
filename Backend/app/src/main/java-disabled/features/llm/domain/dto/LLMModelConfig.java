package org.example.features.llm.domain.dto;

/**
 * DTO for LLM model configuration
 */
public class LLMModelConfig {

    private String modelName;
    private Double temperature;
    private Integer maxTokens;
    private Double topP;
    private Integer contextWindow;

    public LLMModelConfig() {}

    public LLMModelConfig(String modelName) {
        this.modelName = modelName;
    }

    // Getters and setters
    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }

    public Double getTemperature() { return temperature; }
    public void setTemperature(Double temperature) { this.temperature = temperature; }

    public Integer getMaxTokens() { return maxTokens; }
    public void setMaxTokens(Integer maxTokens) { this.maxTokens = maxTokens; }

    public Double getTopP() { return topP; }
    public void setTopP(Double topP) { this.topP = topP; }

    public Integer getContextWindow() { return contextWindow; }
    public void setContextWindow(Integer contextWindow) { this.contextWindow = contextWindow; }
}