package org.example.domain.dto.llm;

import org.example.domain.entities.LLMProvider;

/**
 * Response DTO for LLM test results
 */
public class LLMTestResponse {
    private String modelName;
    private LLMProvider provider;
    private String prompt;
    private String response;
    private Integer tokensUsed;
    private Double responseTimeMs;
    private boolean success;
    
    public LLMTestResponse() {}
    
    public LLMTestResponse(String modelName, LLMProvider provider, String prompt,
                         String response, Integer tokensUsed, Double responseTimeMs, boolean success) {
        this.modelName = modelName;
        this.provider = provider;
        this.prompt = prompt;
        this.response = response;
        this.tokensUsed = tokensUsed;
        this.responseTimeMs = responseTimeMs;
        this.success = success;
    }
    
    // Getters and setters
    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }
    
    public LLMProvider getProvider() { return provider; }
    public void setProvider(LLMProvider provider) { this.provider = provider; }
    
    public String getPrompt() { return prompt; }
    public void setPrompt(String prompt) { this.prompt = prompt; }
    
    public String getResponse() { return response; }
    public void setResponse(String response) { this.response = response; }
    
    public Integer getTokensUsed() { return tokensUsed; }
    public void setTokensUsed(Integer tokensUsed) { this.tokensUsed = tokensUsed; }
    
    public Double getResponseTimeMs() { return responseTimeMs; }
    public void setResponseTimeMs(Double responseTimeMs) { this.responseTimeMs = responseTimeMs; }
    
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
}