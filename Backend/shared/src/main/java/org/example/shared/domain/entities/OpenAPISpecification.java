package org.example.shared.domain.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity representing an OpenAPI specification
 */
@Entity
@Table(name = "openapi_specifications")
public class OpenAPISpecification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    @Lob
    private String content;

    private String version = "3.0.0";

    @Enumerated(EnumType.STRING)
    private SpecificationStatus status = SpecificationStatus.DRAFT;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String createdBy;
    private String lastModifiedBy;

    // Constructors
    public OpenAPISpecification() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public OpenAPISpecification(String title, String content) {
        this();
        this.title = title;
        this.content = content;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public SpecificationStatus getStatus() { return status; }
    public void setStatus(SpecificationStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public String getLastModifiedBy() { return lastModifiedBy; }
    public void setLastModifiedBy(String lastModifiedBy) { this.lastModifiedBy = lastModifiedBy; }

    public enum SpecificationStatus {
        DRAFT,
        PUBLISHED,
        DEPRECATED,
        ARCHIVED
    }
}