package org.example.domain.dto.llm;

/**
 * Request DTO for chat completion
 */
public class ChatCompletionRequest {
    private String modelName;
    private String prompt;
    private Integer maxTokens;
    private Double temperature;
    
    public ChatCompletionRequest() {}
    
    public ChatCompletionRequest(String modelName, String prompt, Integer maxTokens, Double temperature) {
        this.modelName = modelName;
        this.prompt = prompt;
        this.maxTokens = maxTokens;
        this.temperature = temperature;
    }
    
    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }
    
    public String getPrompt() { return prompt; }
    public void setPrompt(String prompt) { this.prompt = prompt; }
    
    public Integer getMaxTokens() { return maxTokens; }
    public void setMaxTokens(Integer maxTokens) { this.maxTokens = maxTokens; }
    
    public Double getTemperature() { return temperature; }
    public void setTemperature(Double temperature) { this.temperature = temperature; }
}