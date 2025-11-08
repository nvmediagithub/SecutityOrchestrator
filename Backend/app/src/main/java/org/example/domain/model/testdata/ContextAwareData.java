package org.example.domain.model.testdata;

import org.example.domain.model.testdata.valueobjects.ContextScope;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Entity representing test data with context-aware information
 */
@Entity
@Table(name = "context_aware_data")
public class ContextAwareData {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String dataId;
    
    @Column(nullable = false, length = 1000)
    private String name;
    
    @Column(length = 2000)
    private String description;
    
    @Column(nullable = false, length = 500)
    private String sourceDataId;
    
    @Embedded
    @Column(nullable = false)
    private ContextScope context;
    
    @ElementCollection
    private Map<String, Object> contextParameters = new HashMap<>();
    
    @ElementCollection
    private Map<String, Object> contextMetadata = new HashMap<>();
    
    @Column(length = 2000)
    private String applicationRules;
    
    @Column(length = 2000)
    private String transformationLogic;
    
    private LocalDateTime validFrom;
    private LocalDateTime validTo;
    private LocalDateTime lastUsed;
    
    private Boolean isDynamic = false;
    private Boolean isCacheable = true;
    private Boolean isShared = false;
    
    private Integer usageCount = 0;
    private Integer priority = 0;
    
    @Column(length = 1000)
    private String dataFormat; // JSON, XML, CSV, PROPERTIES, etc.
    
    @Lob
    @Column(nullable = false)
    private String contextData;
    
    @ElementCollection
    private List<String> dependentProcesses = new ArrayList<>();
    
    @ElementCollection
    private List<String> tags = new ArrayList<>();
    
    @Enumerated(EnumType.STRING)
    private ContextStatus status = ContextStatus.ACTIVE;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    private String createdBy;
    private String lastModifiedBy;
    
    @Column(length = 2000)
    private String notes;
    
    // Constructors
    public ContextAwareData() {
        this.dataId = "CAD_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 10000);
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.contextParameters = new HashMap<>();
        this.contextMetadata = new HashMap<>();
        this.dependentProcesses = new ArrayList<>();
        this.tags = new ArrayList<>();
    }
    
    public ContextAwareData(String name, String sourceDataId, ContextScope context, String contextData) {
        this();
        this.name = name;
        this.sourceDataId = sourceDataId;
        this.context = context;
        this.contextData = contextData;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getDataId() { return dataId; }
    public void setDataId(String dataId) { this.dataId = dataId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getSourceDataId() { return sourceDataId; }
    public void setSourceDataId(String sourceDataId) { this.sourceDataId = sourceDataId; }
    
    public ContextScope getContext() { return context; }
    public void setContext(ContextScope context) { this.context = context; }
    
    public Map<String, Object> getContextParameters() { return contextParameters; }
    public void setContextParameters(Map<String, Object> contextParameters) { this.contextParameters = contextParameters; }
    
    public Map<String, Object> getContextMetadata() { return contextMetadata; }
    public void setContextMetadata(Map<String, Object> contextMetadata) { this.contextMetadata = contextMetadata; }
    
    public String getApplicationRules() { return applicationRules; }
    public void setApplicationRules(String applicationRules) { this.applicationRules = applicationRules; }
    
    public String getTransformationLogic() { return transformationLogic; }
    public void setTransformationLogic(String transformationLogic) { this.transformationLogic = transformationLogic; }
    
    public LocalDateTime getValidFrom() { return validFrom; }
    public void setValidFrom(LocalDateTime validFrom) { this.validFrom = validFrom; }
    
    public LocalDateTime getValidTo() { return validTo; }
    public void setValidTo(LocalDateTime validTo) { this.validTo = validTo; }
    
    public LocalDateTime getLastUsed() { return lastUsed; }
    public void setLastUsed(LocalDateTime lastUsed) { this.lastUsed = lastUsed; }
    
    public Boolean getIsDynamic() { return isDynamic; }
    public void setIsDynamic(Boolean isDynamic) { this.isDynamic = isDynamic; }
    
    public Boolean getIsCacheable() { return isCacheable; }
    public void setIsCacheable(Boolean isCacheable) { this.isCacheable = isCacheable; }
    
    public Boolean getIsShared() { return isShared; }
    public void setIsShared(Boolean isShared) { this.isShared = isShared; }
    
    public Integer getUsageCount() { return usageCount; }
    public void setUsageCount(Integer usageCount) { this.usageCount = usageCount; }
    
    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }
    
    public String getDataFormat() { return dataFormat; }
    public void setDataFormat(String dataFormat) { this.dataFormat = dataFormat; }
    
    public String getContextData() { return contextData; }
    public void setContextData(String contextData) { this.contextData = contextData; }
    
    public List<String> getDependentProcesses() { return dependentProcesses; }
    public void setDependentProcesses(List<String> dependentProcesses) { this.dependentProcesses = dependentProcesses; }
    
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    
    public ContextStatus getStatus() { return status; }
    public void setStatus(ContextStatus status) { this.status = status; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    
    public String getLastModifiedBy() { return lastModifiedBy; }
    public void setLastModifiedBy(String lastModifiedBy) { this.lastModifiedBy = lastModifiedBy; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    // Helper methods
    public boolean isValid() {
        return status == ContextStatus.ACTIVE && 
               (validFrom == null || !LocalDateTime.now().isBefore(validFrom)) &&
               (validTo == null || !LocalDateTime.now().isAfter(validTo));
    }
    
    public boolean isExpired() {
        return validTo != null && LocalDateTime.now().isAfter(validTo);
    }
    
    public boolean isInFuture() {
        return validFrom != null && LocalDateTime.now().isBefore(validFrom);
    }
    
    public boolean canCache() {
        return isCacheable != null && isCacheable && !isExpired();
    }
    
    public boolean canShare() {
        return isShared != null && isShared && isValid();
    }
    
    public boolean isDynamicData() {
        return isDynamic != null && isDynamic;
    }
    
    public boolean hasDependentProcesses() {
        return dependentProcesses != null && !dependentProcesses.isEmpty();
    }
    
    public void addContextParameter(String key, Object value) {
        this.contextParameters.put(key, value);
        this.updatedAt = LocalDateTime.now();
    }
    
    public void addContextMetadata(String key, Object value) {
        this.contextMetadata.put(key, value);
        this.updatedAt = LocalDateTime.now();
    }
    
    public void addDependentProcess(String processId) {
        if (processId != null && !dependentProcesses.contains(processId)) {
            dependentProcesses.add(processId);
        }
    }
    
    public void addTag(String tag) {
        if (tag != null && !tags.contains(tag)) {
            tags.add(tag);
        }
    }
    
    public void incrementUsage() {
        this.usageCount++;
        this.lastUsed = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public void markAsUsed() {
        this.lastUsed = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        incrementUsage();
    }
    
    public void setValidityPeriod(LocalDateTime from, LocalDateTime to) {
        this.validFrom = from;
        this.validTo = to;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void deactivate() {
        this.status = ContextStatus.INACTIVE;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void activate() {
        this.status = ContextStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void expire() {
        this.status = ContextStatus.EXPIRED;
        this.updatedAt = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return "ContextAwareData{" +
                "dataId='" + dataId + '\'' +
                ", name='" + name + '\'' +
                ", sourceDataId='" + sourceDataId + '\'' +
                ", context=" + context +
                ", status=" + status +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContextAwareData that = (ContextAwareData) o;
        return Objects.equals(dataId, that.dataId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(dataId);
    }
    
    // Enums
    public enum ContextStatus {
        ACTIVE,
        INACTIVE,
        EXPIRED,
        DEPRECATED,
        IN_TRANSIT,
        UNDER_REVIEW
    }
}