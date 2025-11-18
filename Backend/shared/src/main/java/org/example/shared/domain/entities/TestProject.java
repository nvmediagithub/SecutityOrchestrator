package org.example.shared.domain.entities;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Domain entity for TestProject
 * Represents a test project in the system
 */
public class TestProject {

    private Long id;
    private String projectId;
    private String name;
    private String description;
    private String owner;
    private ProjectStatus status = ProjectStatus.DRAFT;
    private boolean isActive = true;
    private String environment;
    private String testFramework;
    private String baseUrl;
    private Integer timeoutMs = 30000;
    private Integer retryCount = 0;
    private boolean parallelExecution = false;
    private Set<String> tags = new HashSet<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastTestExecution;
    private Integer artifactCount = 0;
    private Integer sessionCount = 0;

    // Constructors
    public TestProject() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.projectId = generateUniqueId();
    }

    public TestProject(String name, String description) {
        this();
        this.name = name;
        this.description = description;
        this.status = ProjectStatus.DRAFT;
    }

    public TestProject(String name, String description, String owner) {
        this(name, description);
        this.owner = owner;
    }

    // Business methods
    public void activate() {
        this.status = ProjectStatus.ACTIVE;
        this.isActive = true;
        updateTimestamp();
    }

    public void deactivate() {
        this.status = ProjectStatus.INACTIVE;
        this.isActive = false;
        updateTimestamp();
    }

    public void archive() {
        this.status = ProjectStatus.ARCHIVED;
        this.isActive = false;
        updateTimestamp();
    }

    public void complete() {
        this.status = ProjectStatus.COMPLETED;
        updateTimestamp();
    }

    public void updateLastTestExecution() {
        this.lastTestExecution = LocalDateTime.now();
        updateTimestamp();
    }

    public void addTag(String tag) {
        if (tag != null && !tag.trim().isEmpty()) {
            this.tags.add(tag.trim());
            updateTimestamp();
        }
    }

    public void removeTag(String tag) {
        this.tags.remove(tag);
        updateTimestamp();
    }

    public void incrementArtifactCount() {
        this.artifactCount++;
        updateTimestamp();
    }

    public void decrementArtifactCount() {
        if (this.artifactCount > 0) {
            this.artifactCount--;
            updateTimestamp();
        }
    }

    public void incrementSessionCount() {
        this.sessionCount++;
        updateTimestamp();
    }

    public void decrementSessionCount() {
        if (this.sessionCount > 0) {
            this.sessionCount--;
            updateTimestamp();
        }
    }

    private void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }

    private String generateUniqueId() {
        return "PRJ-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        updateTimestamp();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        updateTimestamp();
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
        updateTimestamp();
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
        updateTimestamp();
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
        updateTimestamp();
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
        updateTimestamp();
    }

    public String getTestFramework() {
        return testFramework;
    }

    public void setTestFramework(String testFramework) {
        this.testFramework = testFramework;
        updateTimestamp();
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        updateTimestamp();
    }

    public Integer getTimeoutMs() {
        return timeoutMs;
    }

    public void setTimeoutMs(Integer timeoutMs) {
        this.timeoutMs = timeoutMs;
        updateTimestamp();
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
        updateTimestamp();
    }

    public boolean isParallelExecution() {
        return parallelExecution;
    }

    public void setParallelExecution(boolean parallelExecution) {
        this.parallelExecution = parallelExecution;
        updateTimestamp();
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
        updateTimestamp();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getLastTestExecution() {
        return lastTestExecution;
    }

    public void setLastTestExecution(LocalDateTime lastTestExecution) {
        this.lastTestExecution = lastTestExecution;
    }

    public Integer getArtifactCount() {
        return artifactCount;
    }

    public void setArtifactCount(Integer artifactCount) {
        this.artifactCount = artifactCount;
    }

    public Integer getSessionCount() {
        return sessionCount;
    }

    public void setSessionCount(Integer sessionCount) {
        this.sessionCount = sessionCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestProject that = (TestProject) o;
        return Objects.equals(projectId, that.projectId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectId);
    }

    @Override
    public String toString() {
        return "TestProject{" +
                "id=" + id +
                ", projectId='" + projectId + '\'' +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", isActive=" + isActive +
                ", owner='" + owner + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    // Enum for project status
    public enum ProjectStatus {
        DRAFT,
        ACTIVE,
        INACTIVE,
        COMPLETED,
        ARCHIVED
    }
}