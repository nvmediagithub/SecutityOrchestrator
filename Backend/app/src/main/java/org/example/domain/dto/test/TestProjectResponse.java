package org.example.domain.dto.test;

import org.example.domain.entities.TestProject;

import java.time.LocalDateTime;
import java.util.List;

public class TestProjectResponse {
    
    private Long id;
    private String projectId;
    private String name;
    private String description;
    private TestProject.ProjectStatus status;
    private String owner;
    private String version;
    private String environment;
    private List<String> tags;
    private String configuration;
    private String settings;
    private String testFramework;
    private String baseUrl;
    private Integer timeoutMs;
    private Integer retryCount;
    private Boolean parallelExecution;
    private Integer artifactCount;
    private Integer sessionCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastTestExecution;
    private Boolean isActive;
    
    public TestProjectResponse() {
    }
    
    public TestProjectResponse(TestProject testProject) {
        if (testProject != null) {
            this.id = testProject.getId();
            this.projectId = testProject.getProjectId();
            this.name = testProject.getName();
            this.description = testProject.getDescription();
            this.status = testProject.getStatus();
            this.owner = testProject.getOwner();
            this.version = testProject.getVersion();
            this.environment = testProject.getEnvironment();
            this.tags = testProject.getTags();
            this.configuration = testProject.getConfiguration();
            this.settings = testProject.getSettings();
            this.testFramework = testProject.getTestFramework();
            this.baseUrl = testProject.getBaseUrl();
            this.timeoutMs = testProject.getTimeoutMs();
            this.retryCount = testProject.getRetryCount();
            this.parallelExecution = testProject.getParallelExecution();
            this.artifactCount = testProject.getArtifactCount();
            this.sessionCount = testProject.getSessionCount();
            this.createdAt = testProject.getCreatedAt();
            this.updatedAt = testProject.getUpdatedAt();
            this.lastTestExecution = testProject.getLastTestExecution();
            this.isActive = testProject.getIsActive();
        }
    }
    
    public static TestProjectResponse from(TestProject testProject) {
        return new TestProjectResponse(testProject);
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public TestProject.ProjectStatus getStatus() { return status; }
    public void setStatus(TestProject.ProjectStatus status) { this.status = status; }
    
    public String getOwner() { return owner; }
    public void setOwner(String owner) { this.owner = owner; }
    
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    
    public String getEnvironment() { return environment; }
    public void setEnvironment(String environment) { this.environment = environment; }
    
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    
    public String getConfiguration() { return configuration; }
    public void setConfiguration(String configuration) { this.configuration = configuration; }
    
    public String getSettings() { return settings; }
    public void setSettings(String settings) { this.settings = settings; }
    
    public String getTestFramework() { return testFramework; }
    public void setTestFramework(String testFramework) { this.testFramework = testFramework; }
    
    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
    
    public Integer getTimeoutMs() { return timeoutMs; }
    public void setTimeoutMs(Integer timeoutMs) { this.timeoutMs = timeoutMs; }
    
    public Integer getRetryCount() { return retryCount; }
    public void setRetryCount(Integer retryCount) { this.retryCount = retryCount; }
    
    public Boolean getParallelExecution() { return parallelExecution; }
    public void setParallelExecution(Boolean parallelExecution) { this.parallelExecution = parallelExecution; }
    
    public Integer getArtifactCount() { return artifactCount; }
    public void setArtifactCount(Integer artifactCount) { this.artifactCount = artifactCount; }
    
    public Integer getSessionCount() { return sessionCount; }
    public void setSessionCount(Integer sessionCount) { this.sessionCount = sessionCount; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public LocalDateTime getLastTestExecution() { return lastTestExecution; }
    public void setLastTestExecution(LocalDateTime lastTestExecution) { this.lastTestExecution = lastTestExecution; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    @Override
    public String toString() {
        return "TestProjectResponse{" +
                "id=" + id +
                ", projectId='" + projectId + '\'' +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", owner='" + owner + '\'' +
                ", artifactCount=" + artifactCount +
                ", sessionCount=" + sessionCount +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                '}';
    }
}