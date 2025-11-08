package org.example.domain.dto.execution;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Контекст выполнения для управления тестовыми сессиями
 */
public class ExecutionContext {
    private String executionId;
    private String sessionId;
    private String testType;
    private String status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Map<String, Object> executionParameters;
    private Map<String, Object> results;
    private List<String> steps;
    private Map<String, String> stepStatus;
    private String environment;
    private String targetSystem;
    private String executor;
    private String version;
    private List<String> dependencies;
    private Map<String, Object> metadata;
    
    public ExecutionContext() {
        this.executionId = "exec-" + System.currentTimeMillis();
        this.startTime = LocalDateTime.now();
        this.status = "INITIALIZED";
        this.executionParameters = new HashMap<>();
        this.results = new HashMap<>();
        this.steps = new java.util.ArrayList<>();
        this.stepStatus = new HashMap<>();
        this.dependencies = new java.util.ArrayList<>();
        this.metadata = new HashMap<>();
        this.version = "1.0";
    }
    
    public enum ExecutionStatus {
        INITIALIZED, RUNNING, COMPLETED, FAILED, CANCELLED, PAUSED
    }
    
    // Getters and Setters
    public String getExecutionId() { return executionId; }
    public void setExecutionId(String executionId) { this.executionId = executionId; }
    
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    
    public String getTestType() { return testType; }
    public void setTestType(String testType) { this.testType = testType; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    
    public Map<String, Object> getExecutionParameters() { return executionParameters; }
    public void setExecutionParameters(Map<String, Object> executionParameters) { this.executionParameters = executionParameters; }
    
    public Map<String, Object> getResults() { return results; }
    public void setResults(Map<String, Object> results) { this.results = results; }
    
    public List<String> getSteps() { return steps; }
    public void setSteps(List<String> steps) { this.steps = steps; }
    
    public Map<String, String> getStepStatus() { return stepStatus; }
    public void setStepStatus(Map<String, String> stepStatus) { this.stepStatus = stepStatus; }
    
    public String getEnvironment() { return environment; }
    public void setEnvironment(String environment) { this.environment = environment; }
    
    public String getTargetSystem() { return targetSystem; }
    public void setTargetSystem(String targetSystem) { this.targetSystem = targetSystem; }
    
    public String getExecutor() { return executor; }
    public void setExecutor(String executor) { this.executor = executor; }
    
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    
    public List<String> getDependencies() { return dependencies; }
    public void setDependencies(List<String> dependencies) { this.dependencies = dependencies; }
    
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    
    public void markCompleted() {
        this.status = "COMPLETED";
        this.endTime = LocalDateTime.now();
    }
    
    public void markFailed() {
        this.status = "FAILED";
        this.endTime = LocalDateTime.now();
    }
}