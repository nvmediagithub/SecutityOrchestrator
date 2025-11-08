package org.example.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Domain entity for Test Projects
 * Represents a testing project that can contain multiple artifacts and test sessions
 */
@Entity
@Table(name = "test_projects")
public class TestProject {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Project ID is required")
    @Column(name = "project_id", unique = true, nullable = false)
    private String projectId;
    
    @NotBlank(message = "Name is required")
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ProjectStatus status = ProjectStatus.DRAFT;
    
    @Column(name = "owner")
    private String owner;
    
    @Column(name = "version")
    private String version;
    
    @Column(name = "environment")
    private String environment;
    
    @ElementCollection
    @CollectionTable(name = "test_project_tags", joinColumns = @JoinColumn(name = "test_project_id"))
    @Column(name = "tag")
    private List<String> tags = new ArrayList<>();
    
    @Lob
    @Column(name = "configuration", columnDefinition = "TEXT")
    private String configuration;
    
    @Lob
    @Column(name = "settings", columnDefinition = "TEXT")
    private String settings;
    
    @Column(name = "test_framework")
    private String testFramework;
    
    @Column(name = "base_url")
    private String baseUrl;
    
    @Column(name = "timeout_ms")
    private Integer timeoutMs;
    
    @Column(name = "retry_count")
    private Integer retryCount;
    
    @Column(name = "parallel_execution", nullable = false)
    private Boolean parallelExecution = false;
    
    @Column(name = "artifact_count", nullable = false)
    private Integer artifactCount = 0;
    
    @Column(name = "session_count", nullable = false)
    private Integer sessionCount = 0;
    
    @NotNull(message = "Creation timestamp is required")
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "last_test_execution")
    private LocalDateTime lastTestExecution;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    // Constructors
    public TestProject() {
        this.projectId = generateUniqueId();
        this.createdAt = LocalDateTime.now();
    }
    
    public TestProject(String name, String description) {
        this();
        this.name = name;
        this.description = description;
    }
    
    public TestProject(String name, String description, String owner) {
        this(name, description);
        this.owner = owner;
    }
    
    // PrePersist and PreUpdate callbacks
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (projectId == null || projectId.trim().isEmpty()) {
            projectId = generateUniqueId();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Business methods
    private String generateUniqueId() {
        return "project-" + UUID.randomUUID().toString();
    }
    
    public void addTag(String tag) {
        if (!tags.contains(tag)) {
            tags.add(tag);
        }
    }
    
    public void removeTag(String tag) {
        tags.remove(tag);
    }
    
    public void activate() {
        this.isActive = true;
    }
    
    public void deactivate() {
        this.isActive = false;
    }
    
    public void markAsActive() {
        this.status = ProjectStatus.ACTIVE;
    }
    
    public void markAsArchived() {
        this.status = ProjectStatus.ARCHIVED;
        this.isActive = false;
    }
    
    public void markAsCompleted() {
        this.status = ProjectStatus.COMPLETED;
    }
    
    public void markAsOnHold() {
        this.status = ProjectStatus.ON_HOLD;
    }
    
    public void updateLastTestExecution() {
        this.lastTestExecution = LocalDateTime.now();
    }
    
    public void incrementArtifactCount() {
        this.artifactCount++;
    }
    
    public void decrementArtifactCount() {
        if (this.artifactCount > 0) {
            this.artifactCount--;
        }
    }
    
    public void incrementSessionCount() {
        this.sessionCount++;
    }
    
    public void decrementSessionCount() {
        if (this.sessionCount > 0) {
            this.sessionCount--;
        }
    }
    
    public boolean isActive() {
        return isActive != null && isActive;
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
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public ProjectStatus getStatus() {
        return status;
    }
    
    public void setStatus(ProjectStatus status) {
        this.status = status;
    }
    
    public String getOwner() {
        return owner;
    }
    
    public void setOwner(String owner) {
        this.owner = owner;
    }
    
    public String getVersion() {
        return version;
    }
    
    public void setVersion(String version) {
        this.version = version;
    }
    
    public String getEnvironment() {
        return environment;
    }
    
    public void setEnvironment(String environment) {
        this.environment = environment;
    }
    
    public List<String> getTags() {
        return tags;
    }
    
    public void setTags(List<String> tags) {
        this.tags = tags;
    }
    
    public String getConfiguration() {
        return configuration;
    }
    
    public void setConfiguration(String configuration) {
        this.configuration = configuration;
    }
    
    public String getSettings() {
        return settings;
    }
    
    public void setSettings(String settings) {
        this.settings = settings;
    }
    
    public String getTestFramework() {
        return testFramework;
    }
    
    public void setTestFramework(String testFramework) {
        this.testFramework = testFramework;
    }
    
    public String getBaseUrl() {
        return baseUrl;
    }
    
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
    
    public Integer getTimeoutMs() {
        return timeoutMs;
    }
    
    public void setTimeoutMs(Integer timeoutMs) {
        this.timeoutMs = timeoutMs;
    }
    
    public Integer getRetryCount() {
        return retryCount;
    }
    
    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }
    
    public Boolean getParallelExecution() {
        return parallelExecution;
    }
    
    public void setParallelExecution(Boolean parallelExecution) {
        this.parallelExecution = parallelExecution;
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
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    // Enums
    public enum ProjectStatus {
        DRAFT,
        ACTIVE,
        ON_HOLD,
        COMPLETED,
        ARCHIVED,
        CANCELLED
    }
    
    @Override
    public String toString() {
        return "TestProject{" +
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
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestProject that = (TestProject) o;
        return projectId != null && projectId.equals(that.projectId);
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}