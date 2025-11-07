package org.example.domain.dto.llm;

import java.util.List;

/**
 * Response DTO for list of local models
 */
public class LocalModelsListResponse {
    private List<LocalModelInfo> models;
    private List<String> loadedModels;
    
    public LocalModelsListResponse() {}
    
    public LocalModelsListResponse(List<LocalModelInfo> models, List<String> loadedModels) {
        this.models = models;
        this.loadedModels = loadedModels;
    }
    
    public List<LocalModelInfo> getModels() { return models; }
    public void setModels(List<LocalModelInfo> models) { this.models = models; }
    
    public List<String> getLoadedModels() { return loadedModels; }
    public void setLoadedModels(List<String> loadedModels) { this.loadedModels = loadedModels; }
}