package org.example.domain.model.datamanagement;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Entity representing a collection of test data with version management
 */
@Entity
@Table(name = "test_data_sets")
public class TestDataSet {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false, length = 255)
    private String dataSetId;
    
    @Column(nullable = false, length = 1000)
    private String name;
    
    @Column(length = 2000)
    private String description;
    
    @Column(length = 255)
    private String versionId;
    
    @Lob
    @Column(nullable = false)
    private String dataContent;
    
    @Lob
    @Column(length = 4000)
    private String metadata;
    
    @Column(length = 100)
    private String privacyClassification;
    
    @Enumerated(EnumType.STRING)
    private DataSetStatus status = DataSetStatus.ACTIVE;
    
    @Column(length = 100)
    private String dataType; // JSON, XML, CSV, PROPERTIES, etc.
    
    @Column(length = 255)
    private String schemaVersion;
    
    @ElementCollection
    private List<String> tags = new ArrayList<>();
    
    @ElementCollection
    private List<String> relatedDataSets = new ArrayList<>();
    
    @ElementCollection
    private Map<String, Object> properties = new HashMap<>();
    
    private Long dataSize; // Size in bytes
    private Integer recordCount;
    
    private Boolean isCompressed = false;
    private Boolean isEncrypted = false;
    private Boolean isArchived = false;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastAccessed;
    
    private String createdBy;
    private String lastModifiedBy;
    private String lastAccessedBy;
    
    @Column(length = 2000)
    private String notes;
    
    // Constructors
    public TestDataSet() {
        this.dataSetId = "TDS_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 10000);
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.tags = new ArrayList<>();
        this.relatedDataSets = new ArrayList<>();
        this.properties = new HashMap<>();
    }
    
    public TestDataSet(String name, String dataContent) {
        this();
        this.name = name;
        this.dataContent = dataContent;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getDataSetId() { return dataSetId; }
    public void setDataSetId(String dataSetId) { this.dataSetId = dataSetId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getVersionId() { return versionId; }
    public void setVersionId(String versionId) { this.versionId = versionId; }
    
    public String getDataContent() { return dataContent; }
    public void setDataContent(String dataContent) { this.dataContent = dataContent; }
    
    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }
    
    public String getPrivacyClassification() { return privacyClassification; }
    public void setPrivacyClassification(String privacyClassification) { this.privacyClassification = privacyClassification; }
    
    public DataSetStatus getStatus() { return status; }
    public void setStatus(DataSetStatus status) { this.status = status; }
    
    public String getDataType() { return dataType; }
    public void setDataType(String dataType) { this.dataType = dataType; }
    
    public String getSchemaVersion() { return schemaVersion; }
    public void setSchemaVersion(String schemaVersion) { this.schemaVersion = schemaVersion; }
    
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    
    public List<String> getRelatedDataSets() { return relatedDataSets; }
    public void setRelatedDataSets(List<String> relatedDataSets) { this.relatedDataSets = relatedDataSets; }
    
    public Map<String, Object> getProperties() { return properties; }
    public void setProperties(Map<String, Object> properties) { this.properties = properties; }
    
    public Long getDataSize() { return dataSize; }
    public void setDataSize(Long dataSize) { this.dataSize = dataSize; }
    
    public Integer getRecordCount() { return recordCount; }
    public void setRecordCount(Integer recordCount) { this.recordCount = recordCount; }
    
    public Boolean getIsCompressed() { return isCompressed; }
    public void setIsCompressed(Boolean isCompressed) { this.isCompressed = isCompressed; }
    
    public Boolean getIsEncrypted() { return isEncrypted; }
    public void setIsEncrypted(Boolean isEncrypted) { this.isEncrypted = isEncrypted; }
    
    public Boolean getIsArchived() { return isArchived; }
    public void setIsArchived(Boolean isArchived) { this.isArchived = isArchived; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public LocalDateTime getLastAccessed() { return lastAccessed; }
    public void setLastAccessed(LocalDateTime lastAccessed) { this.lastAccessed = lastAccessed; }
    
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    
    public String getLastModifiedBy() { return lastModifiedBy; }
    public void setLastModifiedBy(String lastModifiedBy) { this.lastModifiedBy = lastModifiedBy; }
    
    public String getLastAccessedBy() { return lastAccessedBy; }
    public void setLastAccessedBy(String lastAccessedBy) { this.lastAccessedBy = lastAccessedBy; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    // Helper methods
    public boolean isActive() {
        return status == DataSetStatus.ACTIVE;
    }
    
    public boolean isValid() {
        return dataContent != null && !dataContent.trim().isEmpty() &&
               name != null && !name.trim().isEmpty();
    }
    
    public boolean hasPrivacyClassification() {
        return privacyClassification != null && !privacyClassification.trim().isEmpty();
    }
    
    public boolean isSensitive() {
        return hasPrivacyClassification() && 
               ("CONFIDENTIAL".equalsIgnoreCase(privacyClassification) ||
                "RESTRICTED".equalsIgnoreCase(privacyClassification) ||
                "PII".equalsIgnoreCase(privacyClassification));
    }
    
    public boolean needsEncryption() {
        return isSensitive() && (isEncrypted == null || !isEncrypted);
    }
    
    public boolean isLarge() {
        return dataSize != null && dataSize > 1024 * 1024; // 1MB
    }
    
    public void addTag(String tag) {
        if (tag != null && !tags.contains(tag)) {
            tags.add(tag);
        }
    }
    
    public void removeTag(String tag) {
        tags.remove(tag);
    }
    
    public void addRelatedDataSet(String dataSetId) {
        if (dataSetId != null && !relatedDataSets.contains(dataSetId)) {
            relatedDataSets.add(dataSetId);
        }
    }
    
    public void removeRelatedDataSet(String dataSetId) {
        relatedDataSets.remove(dataSetId);
    }
    
    public void addProperty(String key, Object value) {
        this.properties.put(key, value);
        this.updatedAt = LocalDateTime.now();
    }
    
    public Object getProperty(String key) {
        return properties.get(key);
    }
    
    public void markAsAccessed(String userId) {
        this.lastAccessed = LocalDateTime.now();
        this.lastAccessedBy = userId;
    }
    
    public void updateContent(String newContent, String modifiedBy) {
        this.dataContent = newContent;
        this.lastModifiedBy = modifiedBy;
        this.updatedAt = LocalDateTime.now();
        if (dataSize != null) {
            this.dataSize = (long) newContent.length();
        }
    }
    
    public void archive() {
        this.isArchived = true;
        this.status = DataSetStatus.ARCHIVED;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void restore() {
        this.isArchived = false;
        this.status = DataSetStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void deactivate() {
        this.status = DataSetStatus.INACTIVE;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void activate() {
        this.status = DataSetStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }
    
    // Enums
    public enum DataSetStatus {
        ACTIVE,
        INACTIVE,
        ARCHIVED,
        DEPRECATED,
        UNDER_REVIEW,
        DRAFT,
        PUBLISHED
    }
    
    @Override
    public String toString() {
        return "TestDataSet{" +
                "dataSetId='" + dataSetId + '\'' +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", privacyClassification='" + privacyClassification + '\'' +
                ", dataType='" + dataType + '\'' +
                ", recordCount=" + recordCount +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestDataSet that = (TestDataSet) o;
        return Objects.equals(dataSetId, that.dataSetId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(dataSetId);
    }
}