package org.example.domain.dto.context;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Контекст для бизнес-логики и правил
 */
public class BusinessContext {
    private String contextId;
    private String name;
    private String description;
    private List<String> businessRules;
    private List<String> businessConstraints;
    private Map<String, Object> businessData;
    private List<String> stakeholders;
    private String domain;
    private List<String> processes;
    private List<String> entities;
    private Map<String, String> relationships;
    private LocalDateTime createdAt;
    private LocalDateTime lastModified;
    private String version;
    
    public BusinessContext() {
        this.contextId = "biz-" + System.currentTimeMillis();
        this.createdAt = LocalDateTime.now();
        this.lastModified = LocalDateTime.now();
        this.version = "1.0";
    }
    
    // Getters and Setters
    public String getContextId() { return contextId; }
    public void setContextId(String contextId) { this.contextId = contextId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public List<String> getBusinessRules() { return businessRules; }
    public void setBusinessRules(List<String> businessRules) { this.businessRules = businessRules; }
    
    public List<String> getBusinessConstraints() { return businessConstraints; }
    public void setBusinessConstraints(List<String> businessConstraints) { this.businessConstraints = businessConstraints; }
    
    public Map<String, Object> getBusinessData() { return businessData; }
    public void setBusinessData(Map<String, Object> businessData) { this.businessData = businessData; }
    
    public List<String> getStakeholders() { return stakeholders; }
    public void setStakeholders(List<String> stakeholders) { this.stakeholders = stakeholders; }
    
    public String getDomain() { return domain; }
    public void setDomain(String domain) { this.domain = domain; }
    
    public List<String> getProcesses() { return processes; }
    public void setProcesses(List<String> processes) { this.processes = processes; }
    
    public List<String> getEntities() { return entities; }
    public void setEntities(List<String> entities) { this.entities = entities; }
    
    public Map<String, String> getRelationships() { return relationships; }
    public void setRelationships(Map<String, String> relationships) { this.relationships = relationships; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getLastModified() { return lastModified; }
    public void setLastModified(LocalDateTime lastModified) { this.lastModified = lastModified; }
    
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
}