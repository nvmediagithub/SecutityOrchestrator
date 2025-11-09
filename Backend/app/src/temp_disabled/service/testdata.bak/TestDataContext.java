package org.example.application.service.testdata;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Контекст для тестовых данных
 */
public class TestDataContext {
    private String contextId;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime lastModified;
    private ContextStatus status;
    private Map<String, Object> requiredData;
    private Map<String, Object> generatedData;
    private List<String> dependencies;
    private Map<String, String> dataTypes;
    private Map<String, String> validationRules;
    private String owner;
    private String version;
    
    public enum ContextStatus {
        CREATED, ACTIVE, PROCESSING, COMPLETED, FAILED, ARCHIVED
    }
    
    public TestDataContext() {
        this.contextId = "ctx-" + System.currentTimeMillis();
        this.createdAt = LocalDateTime.now();
        this.lastModified = LocalDateTime.now();
        this.status = ContextStatus.CREATED;
        this.requiredData = new HashMap<>();
        this.generatedData = new HashMap<>();
        this.dependencies = new java.util.ArrayList<>();
        this.dataTypes = new HashMap<>();
        this.validationRules = new HashMap<>();
        this.version = "1.0";
    }
    
    // Getters and Setters
    public String getContextId() { return contextId; }
    public void setContextId(String contextId) { this.contextId = contextId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getLastModified() { return lastModified; }
    public void setLastModified(LocalDateTime lastModified) { this.lastModified = lastModified; }
    
    public ContextStatus getStatus() { return status; }
    public void setStatus(ContextStatus status) { this.status = status; }
    
    public Map<String, Object> getRequiredData() { return requiredData; }
    public void setRequiredData(Map<String, Object> requiredData) { this.requiredData = requiredData; }
    
    public Map<String, Object> getGeneratedData() { return generatedData; }
    public void setGeneratedData(Map<String, Object> generatedData) { this.generatedData = generatedData; }
    
    public List<String> getDependencies() { return dependencies; }
    public void setDependencies(List<String> dependencies) { this.dependencies = dependencies; }
    
    public Map<String, String> getDataTypes() { return dataTypes; }
    public void setDataTypes(Map<String, String> dataTypes) { this.dataTypes = dataTypes; }
    
    public Map<String, String> getValidationRules() { return validationRules; }
    public void setValidationRules(Map<String, String> validationRules) { this.validationRules = validationRules; }
    
    public String getOwner() { return owner; }
    public void setOwner(String owner) { this.owner = owner; }
    
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
}