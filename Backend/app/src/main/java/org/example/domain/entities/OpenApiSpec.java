package org.example.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.example.domain.valueobjects.OpenApiVersion;
import org.example.domain.valueobjects.SpecificationId;
import org.example.domain.valueobjects.Version;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Domain entity for OpenAPI/Swagger specifications
 * Represents an OpenAPI specification artifact that can be used for automated API testing
 */
@Entity
@Table(name = "openapi_specs")
public class OpenApiSpec {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "value", column = @Column(name = "specification_id", unique = true, nullable = false))
    })
    private SpecificationId specificationId;
    
    @NotBlank(message = "Title is required")
    @Column(name = "title", nullable = false)
    private String title;
    
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "major", column = @Column(name = "version_major")),
        @AttributeOverride(name = "minor", column = @Column(name = "version_minor")),
        @AttributeOverride(name = "patch", column = @Column(name = "version_patch"))
    })
    private Version version;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "openapi_version", nullable = false)
    private OpenApiVersion openApiVersion;
    
    @Lob
    @Column(name = "specification_content", nullable = false, columnDefinition = "TEXT")
    private String specificationContent;
    
    @ElementCollection
    @CollectionTable(name = "openapi_paths", joinColumns = @JoinColumn(name = "openapi_spec_id"))
    @MapKeyColumn(name = "path")
    @Column(name = "path_data", columnDefinition = "TEXT")
    private Map<String, String> paths = new HashMap<>();
    
    @Lob
    @Column(name = "components", columnDefinition = "TEXT")
    private String components;
    
    @Column(name = "server_urls", columnDefinition = "TEXT")
    private String serverUrls;
    
    @Column(name = "description")
    private String description;
    
    @NotNull(message = "Creation timestamp is required")
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "file_name")
    private String fileName;
    
    @Column(name = "file_size")
    private Long fileSize;
    
    @Column(name = "content_type")
    private String contentType;
    
    // Constructors
    public OpenApiSpec() {
        this.specificationId = new SpecificationId(generateUniqueId());
        this.version = new Version(1, 0, 0);
        this.createdAt = LocalDateTime.now();
    }
    
    public OpenApiSpec(String title, OpenApiVersion openApiVersion, String specificationContent) {
        this();
        this.title = title;
        this.openApiVersion = openApiVersion;
        this.specificationContent = specificationContent;
    }
    
    // PrePersist and PreUpdate callbacks
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (specificationId == null || specificationId.getValue() == null) {
            specificationId = new SpecificationId(generateUniqueId());
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Business methods
    private String generateUniqueId() {
        return "openapi-" + java.util.UUID.randomUUID().toString();
    }
    
    public void addPath(String path, String pathData) {
        this.paths.put(path, pathData);
    }
    
    public void removePath(String path) {
        this.paths.remove(path);
    }
    
    public void activate() {
        this.isActive = true;
    }
    
    public void deactivate() {
        this.isActive = false;
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
    
    public SpecificationId getSpecificationId() {
        return specificationId;
    }
    
    public void setSpecificationId(SpecificationId specificationId) {
        this.specificationId = specificationId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public Version getVersion() {
        return version;
    }
    
    public void setVersion(Version version) {
        this.version = version;
    }
    
    public OpenApiVersion getOpenApiVersion() {
        return openApiVersion;
    }
    
    public void setOpenApiVersion(OpenApiVersion openApiVersion) {
        this.openApiVersion = openApiVersion;
    }
    
    public String getSpecificationContent() {
        return specificationContent;
    }
    
    public void setSpecificationContent(String specificationContent) {
        this.specificationContent = specificationContent;
    }
    
    public Map<String, String> getPaths() {
        return paths;
    }
    
    public void setPaths(Map<String, String> paths) {
        this.paths = paths;
    }
    
    public String getComponents() {
        return components;
    }
    
    public void setComponents(String components) {
        this.components = components;
    }
    
    public String getServerUrls() {
        return serverUrls;
    }
    
    public void setServerUrls(String serverUrls) {
        this.serverUrls = serverUrls;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
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
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public Long getFileSize() {
        return fileSize;
    }
    
    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
    
    public String getContentType() {
        return contentType;
    }
    
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    
    @Override
    public String toString() {
        return "OpenApiSpec{" +
                "id=" + id +
                ", specificationId=" + specificationId +
                ", title='" + title + '\'' +
                ", version=" + version +
                ", openApiVersion=" + openApiVersion +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OpenApiSpec that = (OpenApiSpec) o;
        return specificationId != null && specificationId.equals(that.specificationId);
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}