package org.example.domain.dto.llm;

/**
 * Response DTO for chat completion
 */
public class ChatCompletionResponse {
    private String modelName;
    private String response;
    private Integer tokensUsed;
    private Double cost;
    private Double responseTimeMs;
    
    public ChatCompletionResponse() {}
    
    public ChatCompletionResponse(String modelName, String response, Integer tokensUsed, Double cost, Double responseTimeMs) {
        this.modelName = modelName;
        this.response = response;
        this.tokensUsed = tokensUsed;
        this.cost = cost;
        this.responseTimeMs = responseTimeMs;
    }
    
    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }
    
    public String getResponse() { return response; }
    public void setResponse(String response) { this.response = response; }
    
    public Integer getTokensUsed() { return tokensUsed; }
    public void setTokensUsed(Integer tokensUsed) { this.tokensUsed = tokensUsed; }
    
    public Double getCost() { return cost; }
    public void setCost(Double cost) { this.cost = cost; }
    
    public Double getResponseTimeMs() { return responseTimeMs; }
    public void setResponseTimeMs(Double responseTimeMs) { this.responseTimeMs = responseTimeMs; }
}