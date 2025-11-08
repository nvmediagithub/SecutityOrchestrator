package org.example.domain.model.testdata;

import org.example.domain.model.testdata.enums.DependencyType;
import org.example.domain.model.testdata.enums.DataType;
import org.example.domain.model.testdata.valueobjects.DependencyStrength;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Entity representing dependencies between test data elements
 */
@Entity
@Table(name = "data_dependencies")
public class DataDependency {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String dependencyId;
    
    @Column(nullable = false, length = 1000)
    private String name;
    
    @Column(length = 2000)
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DependencyType dependencyType;
    
    @Column(nullable = false, length = 500)
    private String sourceDataId;
    
    @Column(nullable = false, length = 500)
    private String targetDataId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DataType dataType;
    
    @Embedded
    @Column(nullable = false)
    private DependencyStrength strength;
    
    @Column(length = 2000)
    private String conditions;
    
    @Column(length = 1000)
    private String direction; // UNIDIRECTIONAL, BIDIRECTIONAL
    
    private Boolean isActive = true;
    private Boolean isOptional = false;
    private Boolean isConditional = false;
    
    @Column(length = 2000)
    private String validationRules;
    
    @Column(length = 2000)
    private String transformationLogic;
    
    @ElementCollection
    private List<String> tags = new ArrayList<>();
    
    private Integer executionOrder = 0;
    private Integer timeoutSeconds;
    
    @Enumerated(EnumType.STRING)
    private DependencyStatus status = DependencyStatus.ACTIVE;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastValidated;
    
    private String createdBy;
    private String validatedBy;
    
    @Column(length = 2000)
    private String notes;
    
    // Constructors
    public DataDependency() {
        this.dependencyId = "DEPD_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 10000);
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.tags = new ArrayList<>();
    }
    
    public DataDependency(String name, DependencyType dependencyType, String sourceDataId, 
                         String targetDataId, DataType dataType, DependencyStrength strength) {
        this();
        this.name = name;
        this.dependencyType = dependencyType;
        this.sourceDataId = sourceDataId;
        this.targetDataId = targetDataId;
        this.dataType = dataType;
        this.strength = strength;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getDependencyId() { return dependencyId; }
    public void setDependencyId(String dependencyId) { this.dependencyId = dependencyId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public DependencyType getDependencyType() { return dependencyType; }
    public void setDependencyType(DependencyType dependencyType) { this.dependencyType = dependencyType; }
    
    public String getSourceDataId() { return sourceDataId; }
    public void setSourceDataId(String sourceDataId) { this.sourceDataId = sourceDataId; }
    
    public String getTargetDataId() { return targetDataId; }
    public void setTargetDataId(String targetDataId) { this.targetDataId = targetDataId; }
    
    public DataType getDataType() { return dataType; }
    public void setDataType(DataType dataType) { this.dataType = dataType; }
    
    public DependencyStrength getStrength() { return strength; }
    public void setStrength(DependencyStrength strength) { this.strength = strength; }
    
    public String getConditions() { return conditions; }
    public void setConditions(String conditions) { this.conditions = conditions; }
    
    public String getDirection() { return direction; }
    public void setDirection(String direction) { this.direction = direction; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public Boolean getIsOptional() { return isOptional; }
    public void setIsOptional(Boolean isOptional) { this.isOptional = isOptional; }
    
    public Boolean getIsConditional() { return isConditional; }
    public void setIsConditional(Boolean isConditional) { this.isConditional = isConditional; }
    
    public String getValidationRules() { return validationRules; }
    public void setValidationRules(String validationRules) { this.validationRules = validationRules; }
    
    public String getTransformationLogic() { return transformationLogic; }
    public void setTransformationLogic(String transformationLogic) { this.transformationLogic = transformationLogic; }
    
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    
    public Integer getExecutionOrder() { return executionOrder; }
    public void setExecutionOrder(Integer executionOrder) { this.executionOrder = executionOrder; }
    
    public Integer getTimeoutSeconds() { return timeoutSeconds; }
    public void setTimeoutSeconds(Integer timeoutSeconds) { this.timeoutSeconds = timeoutSeconds; }
    
    public DependencyStatus getStatus() { return status; }
    public void setStatus(DependencyStatus status) { this.status = status; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public LocalDateTime getLastValidated() { return lastValidated; }
    public void setLastValidated(LocalDateTime lastValidated) { this.lastValidated = lastValidated; }
    
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    
    public String getValidatedBy() { return validatedBy; }
    public void setValidatedBy(String validatedBy) { this.validatedBy = validatedBy; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    // Helper methods
    public boolean isValid() {
        return status == DependencyStatus.ACTIVE && isActive != null && isActive;
    }
    
    public boolean isCritical() {
        return strength.isCritical() && dependencyType.isStrict();
    }
    
    public boolean isBidirectional() {
        return "BIDIRECTIONAL".equalsIgnoreCase(direction);
    }
    
    public boolean hasConditions() {
        return isConditional != null && isConditional && 
               conditions != null && !conditions.trim().isEmpty();
    }
    
    public boolean hasTimeout() {
        return timeoutSeconds != null && timeoutSeconds > 0;
    }
    
    public void addTag(String tag) {
        if (tag != null && !tags.contains(tag)) {
            tags.add(tag);
        }
    }
    
    public void removeTag(String tag) {
        tags.remove(tag);
    }
    
    public void markAsValidated(String validatedBy) {
        this.lastValidated = LocalDateTime.now();
        this.validatedBy = validatedBy;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void deactivate() {
        this.isActive = false;
        this.status = DependencyStatus.INACTIVE;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void activate() {
        this.isActive = true;
        this.status = DependencyStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void setAsError(String errorMessage) {
        this.status = DependencyStatus.ERROR;
        this.notes = (notes != null ? notes + "\n" : "") + "Error: " + errorMessage;
        this.updatedAt = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return "DataDependency{" +
                "dependencyId='" + dependencyId + '\'' +
                ", name='" + name + '\'' +
                ", dependencyType=" + dependencyType +
                ", sourceDataId='" + sourceDataId + '\'' +
                ", targetDataId='" + targetDataId + '\'' +
                ", dataType=" + dataType +
                ", strength=" + strength +
                ", status=" + status +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataDependency that = (DataDependency) o;
        return Objects.equals(dependencyId, that.dependencyId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(dependencyId);
    }
    
    // Enums
    public enum DependencyStatus {
        ACTIVE,
        INACTIVE,
        ERROR,
        DEPRECATED,
        VALIDATION_FAILED
    }
}