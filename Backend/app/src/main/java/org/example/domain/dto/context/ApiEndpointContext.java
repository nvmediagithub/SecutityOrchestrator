package org.example.domain.dto.context;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Контекст для анализа отдельного API endpoint
 */
public class ApiEndpointContext {
    private String specId;
    private String endpointId;
    private String method;
    private String path;
    private String operationId;
    private String summary;
    private String description;
    private List<String> tags;
    private Map<String, Object> parameters;
    private List<String> requestBodySchemas;
    private List<String> responseSchemas;
    private List<String> securityRequirements;
    private LocalDateTime analyzedAt;
    private List<String> dependencies;
    private List<String> dataFlows;
    
    public ApiEndpointContext() {
        this.analyzedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public String getSpecId() { return specId; }
    public void setSpecId(String specId) { this.specId = specId; }
    
    public String getEndpointId() { return endpointId; }
    public void setEndpointId(String endpointId) { this.endpointId = endpointId; }
    
    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }
    
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
    
    public String getOperationId() { return operationId; }
    public void setOperationId(String operationId) { this.operationId = operationId; }
    
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    
    public Map<String, Object> getParameters() { return parameters; }
    public void setParameters(Map<String, Object> parameters) { this.parameters = parameters; }
    
    public List<String> getRequestBodySchemas() { return requestBodySchemas; }
    public void setRequestBodySchemas(List<String> requestBodySchemas) { this.requestBodySchemas = requestBodySchemas; }
    
    public List<String> getResponseSchemas() { return responseSchemas; }
    public void setResponseSchemas(List<String> responseSchemas) { this.responseSchemas = responseSchemas; }
    
    public List<String> getSecurityRequirements() { return securityRequirements; }
    public void setSecurityRequirements(List<String> securityRequirements) { this.securityRequirements = securityRequirements; }
    
    public LocalDateTime getAnalyzedAt() { return analyzedAt; }
    public void setAnalyzedAt(LocalDateTime analyzedAt) { this.analyzedAt = analyzedAt; }
    
    public List<String> getDependencies() { return dependencies; }
    public void setDependencies(List<String> dependencies) { this.dependencies = dependencies; }
    
    public List<String> getDataFlows() { return dataFlows; }
    public void setDataFlows(List<String> dataFlows) { this.dataFlows = dataFlows; }
}