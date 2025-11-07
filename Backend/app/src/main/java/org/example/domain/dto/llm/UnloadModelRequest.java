package org.example.domain.dto.llm;

/**
 * Request DTO for unloading local model
 */
public class UnloadModelRequest {
    private String modelName;
    
    public UnloadModelRequest() {}
    
    public UnloadModelRequest(String modelName) {
        this.modelName = modelName;
    }
    
    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }
}