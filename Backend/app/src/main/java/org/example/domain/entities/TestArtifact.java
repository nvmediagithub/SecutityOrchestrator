package org.example.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Domain entity for Test Artifacts
 * Represents the relationship between TestProjects and artifacts (OpenApiSpec, BpmnDiagram)
 * This is a linking entity that allows multiple artifacts to be associated with multiple projects
 */
@Entity
@Table(name = "test_artifacts")
public class TestArtifact {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "Artifact ID is required")
    @Column(name = "artifact_id", unique = true, nullable = false)
    private String artifactId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_project_id", nullable = false)
    private TestProject testProject;
    
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "openapi_spec_id")
    private OpenApiSpec openApiSpec;
    
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "bpmn_diagram_id")
    private BpmnDiagram bpmnDiagram;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "artifact_type", nullable = false)
    private ArtifactType artifactType;
    
    @Column(name = "artifact_name")
    private String artifactName;
    
    @Column(name = "artifact_description", columnDefinition = "TEXT")
    private String artifactDescription;
    
    @Column(name = "version")
    private String version;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ArtifactStatus status = ArtifactStatus.ACTIVE;
    
    @Lob
    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata;
    
    @Column(name = "uploaded_by")
    private String uploadedBy;
    
    @ElementCollection
    @CollectionTable(name = "test_artifact_tags", joinColumns = @JoinColumn(name = "test_artifact_id"))
    @Column(name = "tag")
    private List<String> tags = new ArrayList<>();
    
    @Column(name = "is_primary", nullable = false)
    private Boolean isPrimary = false;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @NotNull(message = "Creation timestamp is required")
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "last_used_at")
    private LocalDateTime lastUsedAt;
    
    @Column(name = "usage_count", nullable = false)
    private Integer usageCount = 0;
    
    @Column(name = "test_session_count", nullable = false)
    private Integer testSessionCount = 0;
    
    // Constructors
    public TestArtifact() {
        this.artifactId = generateUniqueId();
        this.createdAt = LocalDateTime.now();
    }
    
    public TestArtifact(TestProject testProject, OpenApiSpec openApiSpec) {
        this();
        this.testProject = testProject;
        this.openApiSpec = openApiSpec;
        this.artifactType = ArtifactType.OPENAPI_SPEC;
        this.artifactName = openApiSpec.getTitle();
        this.version = openApiSpec.getVersion() != null ? 
            openApiSpec.getVersion().toString() : "1.0.0";
    }
    
    public TestArtifact(TestProject testProject, BpmnDiagram bpmnDiagram) {
        this();
        this.testProject = testProject;
        this.bpmnDiagram = bpmnDiagram;
        this.artifactType = ArtifactType.BPMN_DIAGRAM;
        this.artifactName = bpmnDiagram.getName();
        this.version = bpmnDiagram.getVersion() != null ? bpmnDiagram.getVersion() : "1.0.0";
    }
    
    // PrePersist and PreUpdate callbacks
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (artifactId == null || artifactId.trim().isEmpty()) {
            artifactId = generateUniqueId();
        }
        updateArtifactNameIfNeeded();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        updateArtifactNameIfNeeded();
    }
    
    // Business methods
    private String generateUniqueId() {
        return "artifact-" + UUID.randomUUID().toString();
    }
    
    private void updateArtifactNameIfNeeded() {
        if (openApiSpec != null && (artifactName == null || artifactName.isEmpty())) {
            artifactName = openApiSpec.getTitle();
        } else if (bpmnDiagram != null && (artifactName == null || artifactName.isEmpty())) {
            artifactName = bpmnDiagram.getName();
        }
    }
    
    public void attachToProject(TestProject testProject) {
        this.testProject = testProject;
        if (testProject != null) {
            testProject.incrementArtifactCount();
        }
    }
    
    public void detachFromProject() {
        if (this.testProject != null) {
            this.testProject.decrementArtifactCount();
            this.testProject = null;
        }
    }
    
    public void markAsPrimary() {
        this.isPrimary = true;
    }
    
    public void unmarkAsPrimary() {
        this.isPrimary = false;
    }
    
    public void activate() {
        this.isActive = true;
        this.status = ArtifactStatus.ACTIVE;
    }
    
    public void deactivate() {
        this.isActive = false;
        this.status = ArtifactStatus.INACTIVE;
    }
    
    public void archive() {
        this.isActive = false;
        this.status = ArtifactStatus.ARCHIVED;
    }
    
    public void markAsDeprecated() {
        this.status = ArtifactStatus.DEPRECATED;
    }
    
    public void incrementUsage() {
        this.usageCount++;
        this.lastUsedAt = LocalDateTime.now();
    }
    
    public void incrementTestSessionCount() {
        this.testSessionCount++;
    }
    
    public void decrementTestSessionCount() {
        if (this.testSessionCount > 0) {
            this.testSessionCount--;
        }
    }
    
    public void addTag(String tag) {
        if (!tags.contains(tag)) {
            tags.add(tag);
        }
    }
    
    public void removeTag(String tag) {
        tags.remove(tag);
    }
    
    public boolean isActive() {
        return isActive != null && isActive;
    }
    
    public boolean isPrimary() {
        return isPrimary != null && isPrimary;
    }
    
    public boolean hasOpenApiSpec() {
        return openApiSpec != null;
    }
    
    public boolean hasBpmnDiagram() {
        return bpmnDiagram != null;
    }
    
    public String getDisplayName() {
        if (artifactName != null && !artifactName.isEmpty()) {
            return artifactName;
        } else if (openApiSpec != null) {
            return openApiSpec.getTitle();
        } else if (bpmnDiagram != null) {
            return bpmnDiagram.getName();
        }
        return "Unnamed Artifact";
    }
    
    public String getVersionString() {
        if (version != null && !version.isEmpty()) {
            return version;
        } else if (openApiSpec != null && openApiSpec.getVersion() != null) {
            return openApiSpec.getVersion().toString();
        } else if (bpmnDiagram != null && bpmnDiagram.getVersion() != null) {
            return bpmnDiagram.getVersion();
        }
        return "1.0.0";
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getArtifactId() {
        return artifactId;
    }
    
    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }
    
    public TestProject getTestProject() {
        return testProject;
    }
    
    public void setTestProject(TestProject testProject) {
        this.testProject = testProject;
    }
    
    public OpenApiSpec getOpenApiSpec() {
        return openApiSpec;
    }
    
    public void setOpenApiSpec(OpenApiSpec openApiSpec) {
        this.openApiSpec = openApiSpec;
        if (openApiSpec != null) {
            this.artifactType = ArtifactType.OPENAPI_SPEC;
        }
    }
    
    public BpmnDiagram getBpmnDiagram() {
        return bpmnDiagram;
    }
    
    public void setBpmnDiagram(BpmnDiagram bpmnDiagram) {
        this.bpmnDiagram = bpmnDiagram;
        if (bpmnDiagram != null) {
            this.artifactType = ArtifactType.BPMN_DIAGRAM;
        }
    }
    
    public ArtifactType getArtifactType() {
        return artifactType;
    }
    
    public void setArtifactType(ArtifactType artifactType) {
        this.artifactType = artifactType;
    }
    
    public String getArtifactName() {
        return artifactName;
    }
    
    public void setArtifactName(String artifactName) {
        this.artifactName = artifactName;
    }
    
    public String getArtifactDescription() {
        return artifactDescription;
    }
    
    public void setArtifactDescription(String artifactDescription) {
        this.artifactDescription = artifactDescription;
    }
    
    public String getVersion() {
        return version;
    }
    
    public void setVersion(String version) {
        this.version = version;
    }
    
    public ArtifactStatus getStatus() {
        return status;
    }
    
    public void setStatus(ArtifactStatus status) {
        this.status = status;
    }
    
    public String getMetadata() {
        return metadata;
    }
    
    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }
    
    public String getUploadedBy() {
        return uploadedBy;
    }
    
    public void setUploadedBy(String uploadedBy) {
        this.uploadedBy = uploadedBy;
    }
    
    public List<String> getTags() {
        return tags;
    }
    
    public void setTags(List<String> tags) {
        this.tags = tags;
    }
    
    public Boolean getIsPrimary() {
        return isPrimary;
    }
    
    public void setIsPrimary(Boolean isPrimary) {
        this.isPrimary = isPrimary;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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
    
    public LocalDateTime getLastUsedAt() {
        return lastUsedAt;
    }
    
    public void setLastUsedAt(LocalDateTime lastUsedAt) {
        this.lastUsedAt = lastUsedAt;
    }
    
    public Integer getUsageCount() {
        return usageCount;
    }
    
    public void setUsageCount(Integer usageCount) {
        this.usageCount = usageCount;
    }
    
    public Integer getTestSessionCount() {
        return testSessionCount;
    }
    
    public void setTestSessionCount(Integer testSessionCount) {
        this.testSessionCount = testSessionCount;
    }
    
    // Enums
    public enum ArtifactType {
        OPENAPI_SPEC,
        BPMN_DIAGRAM,
        OTHER
    }
    
    public enum ArtifactStatus {
        ACTIVE,
        INACTIVE,
        ARCHIVED,
        DEPRECATED,
        UNDER_REVIEW
    }
    
    @Override
    public String toString() {
        return "TestArtifact{" +
                "id=" + id +
                ", artifactId='" + artifactId + '\'' +
                ", artifactType=" + artifactType +
                ", artifactName='" + getDisplayName() + '\'' +
                ", version='" + getVersionString() + '\'' +
                ", status=" + status +
                ", isPrimary=" + isPrimary +
                ", isActive=" + isActive +
                ", usageCount=" + usageCount +
                ", testSessionCount=" + testSessionCount +
                ", createdAt=" + createdAt +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestArtifact that = (TestArtifact) o;
        return artifactId != null && artifactId.equals(that.artifactId);
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}