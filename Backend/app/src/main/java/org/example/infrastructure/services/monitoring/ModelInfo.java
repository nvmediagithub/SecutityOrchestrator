package org.example.infrastructure.services.monitoring;

/**
 * Information about active models in the system
 */
public class ModelInfo {
    private final String modelId;
    private final String modelName;
    private final String provider;
    private final String status;
    private final Double sizeGB;
    
    public ModelInfo(String modelId, String modelName, String provider, 
                    String status, Double sizeGB) {
        this.modelId = modelId;
        this.modelName = modelName;
        this.provider = provider;
        this.status = status;
        this.sizeGB = sizeGB;
    }
    
    public String getModelId() {
        return modelId;
    }
    
    public String getModelName() {
        return modelName;
    }
    
    public String getProvider() {
        return provider;
    }
    
    public String getStatus() {
        return status;
    }
    
    public Double getSizeGB() {
        return sizeGB;
    }
    
    @Override
    public String toString() {
        return "ModelInfo{" +
                "modelId='" + modelId + '\'' +
                ", modelName='" + modelName + '\'' +
                ", provider='" + provider + '\'' +
                ", status='" + status + '\'' +
                ", sizeGB=" + sizeGB +
                '}';
    }
}