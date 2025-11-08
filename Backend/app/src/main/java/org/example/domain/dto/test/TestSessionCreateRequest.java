package org.example.domain.dto.test;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.example.domain.entities.TestSession;

import java.util.List;

/**
 * DTO for creating test sessions
 */
public class TestSessionCreateRequest {
    
    @NotBlank(message = "Name is required")
    @Size(min = 1, max = 255, message = "Name must be between 1 and 255 characters")
    private String name;
    
    private String description;
    
    private String executor;
    
    private String environment;
    
    private String testType;
    
    private String testConfig;
    
    private List<String> artifactIds;
    
    private List<String> tags;
    
    private Integer timeoutMinutes;
    
    private Boolean retryOnFailure;
    
    private Boolean parallelExecution;
    
    // Constructors
    public TestSessionCreateRequest() {
    }
    
    public TestSessionCreateRequest(String name, String description) {
        this.name = name;
        this.description = description;
    }
    
    public TestSessionCreateRequest(String name, String description, String executor) {
        this(name, description);
        this.executor = executor;
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getExecutor() {
        return executor;
    }
    
    public void setExecutor(String executor) {
        this.executor = executor;
    }
    
    public String getEnvironment() {
        return environment;
    }
    
    public void setEnvironment(String environment) {
        this.environment = environment;
    }
    
    public String getTestType() {
        return testType;
    }
    
    public void setTestType(String testType) {
        this.testType = testType;
    }
    
    public String getTestConfig() {
        return testConfig;
    }
    
    public void setTestConfig(String testConfig) {
        this.testConfig = testConfig;
    }
    
    public List<String> getArtifactIds() {
        return artifactIds;
    }
    
    public void setArtifactIds(List<String> artifactIds) {
        this.artifactIds = artifactIds;
    }
    
    public List<String> getTags() {
        return tags;
    }
    
    public void setTags(List<String> tags) {
        this.tags = tags;
    }
    
    public Integer getTimeoutMinutes() {
        return timeoutMinutes;
    }
    
    public void setTimeoutMinutes(Integer timeoutMinutes) {
        this.timeoutMinutes = timeoutMinutes;
    }
    
    public Boolean getRetryOnFailure() {
        return retryOnFailure;
    }
    
    public void setRetryOnFailure(Boolean retryOnFailure) {
        this.retryOnFailure = retryOnFailure;
    }
    
    public Boolean getParallelExecution() {
        return parallelExecution;
    }
    
    public void setParallelExecution(Boolean parallelExecution) {
        this.parallelExecution = parallelExecution;
    }
    
    @Override
    public String toString() {
        return "TestSessionCreateRequest{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", executor='" + executor + '\'' +
                ", environment='" + environment + '\'' +
                ", testType='" + testType + '\'' +
                ", artifactIds=" + artifactIds +
                ", tags=" + tags +
                ", timeoutMinutes=" + timeoutMinutes +
                ", retryOnFailure=" + retryOnFailure +
                ", parallelExecution=" + parallelExecution +
                '}';
    }
}