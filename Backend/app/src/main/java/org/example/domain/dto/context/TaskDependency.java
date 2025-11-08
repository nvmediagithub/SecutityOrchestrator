package org.example.domain.dto.context;

/**
 * Зависимость между задачами в BPMN
 */
public class TaskDependency {
    
    private String dependencyId;
    private String sourceTaskId;
    private String targetTaskId;
    private String dependencyType; // SEQUENTIAL, PARALLEL, CONDITIONAL, MESSAGE
    private String condition;
    
    public TaskDependency() {
        this.dependencyId = "task_dep_" + System.currentTimeMillis();
    }
    
    // Getters and Setters
    public String getDependencyId() { return dependencyId; }
    public void setDependencyId(String dependencyId) { this.dependencyId = dependencyId; }
    
    public String getSourceTaskId() { return sourceTaskId; }
    public void setSourceTaskId(String sourceTaskId) { this.sourceTaskId = sourceTaskId; }
    
    public String getTargetTaskId() { return targetTaskId; }
    public void setTargetTaskId(String targetTaskId) { this.targetTaskId = targetTaskId; }
    
    public String getDependencyType() { return dependencyType; }
    public void setDependencyType(String dependencyType) { this.dependencyType = dependencyType; }
    
    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }
}