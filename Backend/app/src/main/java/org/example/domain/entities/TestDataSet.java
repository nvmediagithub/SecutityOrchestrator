package org.example.domain.entities;

import org.example.domain.valueobjects.SeverityLevel;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Сущность для хранения тестовых данных для сценариев
 */
@Entity
@Table(name = "test_data_sets")
public class TestDataSet {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String dataSetId;
    
    @Column(nullable = false)
    private String name;
    
    @Column(length = 2000)
    private String description;
    
    @Column(length = 1000)
    private String dataType; // JSON, XML, FORM, CSV, DATABASE, API_RESPONSE
    
    @Column(length = 1000)
    private String category; // POSITIVE, NEGATIVE, BOUNDARY, RANDOM, SECURED, MOCK
    
    @Column(nullable = false)
    @Lob
    private String testData;
    
    @Column(length = 1000)
    private String source; // GENERATED, IMPORTED, RECORDED, MOCK, TEMPLATE
    
    @Column(length = 2000)
    private String template;
    
    @Column(length = 1000)
    private String environment; // DEV, TEST, STAGING, PROD
    
    @Enumerated(EnumType.STRING)
    private SensitivityLevel sensitivityLevel = SensitivityLevel.PUBLIC;
    
    private Boolean isEncrypted = false;
    private Boolean isDynamic = false;
    private Boolean isReusable = true;
    private Boolean isMasked = false;
    
    @Column(length = 2000)
    private String dataSchema;
    
    @Column(length = 2000)
    private String validationRules;
    
    @Column(length = 2000)
    private String transformationRules;
    
    @Column(length = 2000)
    private String dependencies;
    
    @ElementCollection
    private List<String> tags = new ArrayList<>();
    
    @ElementCollection
    private List<String> requiredVariables = new ArrayList<>();
    
    @ElementCollection
    private List<String> generatedFields = new ArrayList<>();
    
    @ElementCollection
    private List<String> maskedFields = new ArrayList<>();
    
    private Integer usageCount = 0;
    private Integer lastUsedAt;
    
    @Enumerated(EnumType.STRING)
    private DataStatus status = DataStatus.ACTIVE;
    
    @Column(length = 1000)
    private String version = "1.0";
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastUsed;
    
    private String createdBy;
    private String lastModifiedBy;
    
    @Column(length = 2000)
    private String notes;
    
    // Constructors
    public TestDataSet() {
        this.dataSetId = "TDS_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 10000);
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.tags = new ArrayList<>();
        this.requiredVariables = new ArrayList<>();
        this.generatedFields = new ArrayList<>();
        this.maskedFields = new ArrayList<>();
    }
    
    public TestDataSet(String name, String dataType, String testData) {
        this();
        this.name = name;
        this.dataType = dataType;
        this.testData = testData;
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
    
    public String getDataType() { return dataType; }
    public void setDataType(String dataType) { this.dataType = dataType; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getTestData() { return testData; }
    public void setTestData(String testData) { this.testData = testData; }
    
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    
    public String getTemplate() { return template; }
    public void setTemplate(String template) { this.template = template; }
    
    public String getEnvironment() { return environment; }
    public void setEnvironment(String environment) { this.environment = environment; }
    
    public SensitivityLevel getSensitivityLevel() { return sensitivityLevel; }
    public void setSensitivityLevel(SensitivityLevel sensitivityLevel) { this.sensitivityLevel = sensitivityLevel; }
    
    public Boolean getIsEncrypted() { return isEncrypted; }
    public void setIsEncrypted(Boolean isEncrypted) { this.isEncrypted = isEncrypted; }
    
    public Boolean getIsDynamic() { return isDynamic; }
    public void setIsDynamic(Boolean isDynamic) { this.isDynamic = isDynamic; }
    
    public Boolean getIsReusable() { return isReusable; }
    public void setIsReusable(Boolean isReusable) { this.isReusable = isReusable; }
    
    public Boolean getIsMasked() { return isMasked; }
    public void setIsMasked(Boolean isMasked) { this.isMasked = isMasked; }
    
    public String getDataSchema() { return dataSchema; }
    public void setDataSchema(String dataSchema) { this.dataSchema = dataSchema; }
    
    public String getValidationRules() { return validationRules; }
    public void setValidationRules(String validationRules) { this.validationRules = validationRules; }
    
    public String getTransformationRules() { return transformationRules; }
    public void setTransformationRules(String transformationRules) { this.transformationRules = transformationRules; }
    
    public String getDependencies() { return dependencies; }
    public void setDependencies(String dependencies) { this.dependencies = dependencies; }
    
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    
    public List<String> getRequiredVariables() { return requiredVariables; }
    public void setRequiredVariables(List<String> requiredVariables) { this.requiredVariables = requiredVariables; }
    
    public List<String> getGeneratedFields() { return generatedFields; }
    public void setGeneratedFields(List<String> generatedFields) { this.generatedFields = generatedFields; }
    
    public List<String> getMaskedFields() { return maskedFields; }
    public void setMaskedFields(List<String> maskedFields) { this.maskedFields = maskedFields; }
    
    public Integer getUsageCount() { return usageCount; }
    public void setUsageCount(Integer usageCount) { this.usageCount = usageCount; }
    
    public Integer getLastUsedAt() { return lastUsedAt; }
    public void setLastUsedAt(Integer lastUsedAt) { this.lastUsedAt = lastUsedAt; }
    
    public DataStatus getStatus() { return status; }
    public void setStatus(DataStatus status) { this.status = status; }
    
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public LocalDateTime getLastUsed() { return lastUsed; }
    public void setLastUsed(LocalDateTime lastUsed) { this.lastUsed = lastUsed; }
    
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    
    public String getLastModifiedBy() { return lastModifiedBy; }
    public void setLastModifiedBy(String lastModifiedBy) { this.lastModifiedBy = lastModifiedBy; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    // Helper methods
    public boolean isJsonData() {
        return dataType != null && dataType.toUpperCase().contains("JSON");
    }
    
    public boolean isXmlData() {
        return dataType != null && dataType.toUpperCase().contains("XML");
    }
    
    public boolean isSensitive() {
        return sensitivityLevel == SensitivityLevel.CONFIDENTIAL || 
               sensitivityLevel == SensitivityLevel.SECRET;
    }
    
    public boolean isExpired() {
        return status == DataStatus.EXPIRED;
    }
    
    public boolean isAvailable() {
        return status == DataStatus.ACTIVE && !isExpired();
    }
    
    public void incrementUsage() {
        this.usageCount++;
        this.lastUsed = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public void addTag(String tag) {
        if (tag != null && !tags.contains(tag)) {
            tags.add(tag);
        }
    }
    
    public void addRequiredVariable(String variable) {
        if (variable != null && !requiredVariables.contains(variable)) {
            requiredVariables.add(variable);
        }
    }
    
    public void addGeneratedField(String field) {
        if (field != null && !generatedFields.contains(field)) {
            generatedFields.add(field);
        }
    }
    
    public void addMaskedField(String field) {
        if (field != null && !maskedFields.contains(field)) {
            maskedFields.add(field);
        }
    }
    
    public void markAsUsed() {
        this.lastUsed = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public void deactivate() {
        this.status = DataStatus.INACTIVE;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void expire() {
        this.status = DataStatus.EXPIRED;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getMaskedData() {
        if (!isMasked) {
            return testData;
        }
        
        String maskedData = testData;
        for (String field : maskedFields) {
            // Simple masking - in real implementation, use proper masking rules
            maskedData = maskedData.replaceAll("\"" + field + "\":\"[^\"]*\"", "\"" + field + "\":\"[MASKED]\"");
        }
        return maskedData;
    }
    
    public boolean hasValidSchema() {
        return dataSchema != null && !dataSchema.trim().isEmpty();
    }
    
    public boolean requiresTransformation() {
        return transformationRules != null && !transformationRules.trim().isEmpty();
    }
    
    @Override
    public String toString() {
        return "TestDataSet{" +
                "dataSetId='" + dataSetId + '\'' +
                ", name='" + name + '\'' +
                ", dataType='" + dataType + '\'' +
                ", category='" + category + '\'' +
                ", status=" + status +
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
    
    // Enums
    public enum DataStatus {
        ACTIVE,
        INACTIVE,
        EXPIRED,
        ARCHIVED,
        DEPRECATED
    }
    
    public enum SensitivityLevel {
        PUBLIC,
        INTERNAL,
        CONFIDENTIAL,
        SECRET
    }
}