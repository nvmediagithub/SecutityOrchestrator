package org.example.domain.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity for storing business processes
 */
@Entity
@Table(name = "business_processes")
public class BusinessProcess {
    
    @Id
    private UUID id;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "version", nullable = false)
    private String version;
    
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ProcessStatus status = ProcessStatus.DRAFT;
    
    @Column(name = "diagram_id")
    private String diagramId;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "created_by", nullable = false)
    private String createdBy;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        this.id = UUID.randomUUID();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    public BusinessProcess() {
    }
    
    public BusinessProcess(String name, String version, String createdBy) {
        this.name = name;
        this.version = version;
        this.createdBy = createdBy;
    }
    
    public enum ProcessStatus {
        DRAFT, ACTIVE, ARCHIVED, PENDING, COMPLETED
    }
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getVersion() {
        return version;
    }
    
    public void setVersion(String version) {
        this.version = version;
    }
    
    public ProcessStatus getStatus() {
        return status;
    }
    
    public void setStatus(ProcessStatus status) {
        this.status = status;
    }
    
    public String getDiagramId() {
        return diagramId;
    }
    
    public void setDiagramId(String diagramId) {
        this.diagramId = diagramId;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
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
}
