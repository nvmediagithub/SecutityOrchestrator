package org.example.domain.entities;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;

/**
 * BPMN Process Step entity for tracking security analysis workflow
 */
public class BpmnProcessStep {
    private String id;
    private String name;
    private String status; // PENDING, RUNNING, COMPLETED, FAILED
    private int executionTime; // in milliseconds
    private LocalDateTime timestamp;
    private String description;
    private Map<String, Object> metadata;
    private String nextStepId;
    private String previousStepId;
    
    public BpmnProcessStep() {
        this.id = generateId();
        this.timestamp = LocalDateTime.now();
        this.status = "PENDING";
        this.metadata = new HashMap<>();
    }
    
    public BpmnProcessStep(String name, String description) {
        this();
        this.name = name;
        this.description = description;
    }
    
    private String generateId() {
        return "BPMN_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public int getExecutionTime() { return executionTime; }
    public void setExecutionTime(int executionTime) { this.executionTime = executionTime; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    
    public String getNextStepId() { return nextStepId; }
    public void setNextStepId(String nextStepId) { this.nextStepId = nextStepId; }
    
    public String getPreviousStepId() { return previousStepId; }
    public void setPreviousStepId(String previousStepId) { this.previousStepId = previousStepId; }
    
    @Override
    public String toString() {
        return "BpmnProcessStep{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", executionTime=" + executionTime +
                '}';
    }
}