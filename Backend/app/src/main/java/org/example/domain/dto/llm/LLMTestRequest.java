package org.example.domain.dto.llm;

/**
 * Request DTO for testing LLM
 */
public class LLMTestRequest {
    private String prompt;
    private String modelName;
    
    public LLMTestRequest() {}
    
    public LLMTestRequest(String prompt, String modelName) {
        this.prompt = prompt;
        this.modelName = modelName;
    }
    
    public String getPrompt() { return prompt; }
    public void setPrompt(String prompt) { this.prompt = prompt; }
    
    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }
}