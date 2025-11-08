package org.example.domain.dto.context;

/**
 * Контекстная зависимость
 */
public class ContextDependency {
    
    private String dependencyId;
    private String sourceId;
    private String targetId;
    private String dependencyType; // DATA_FLOW, PROCESS, API, BUSINESS
    private String description;
    private boolean isStrong;
    
    public ContextDependency() {
        this.dependencyId = "ctx_dep_" + System.currentTimeMillis();
    }
    
    // Getters and Setters
    public String getDependencyId() { return dependencyId; }
    public void setDependencyId(String dependencyId) { this.dependencyId = dependencyId; }
    
    public String getSourceId() { return sourceId; }
    public void setSourceId(String sourceId) { this.sourceId = sourceId; }
    
    public String getTargetId() { return targetId; }
    public void setTargetId(String targetId) { this.targetId = targetId; }
    
    public String getDependencyType() { return dependencyType; }
    public void setDependencyType(String dependencyType) { this.dependencyType = dependencyType; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public boolean isStrong() { return isStrong; }
    public void setStrong(boolean strong) { isStrong = strong; }
}