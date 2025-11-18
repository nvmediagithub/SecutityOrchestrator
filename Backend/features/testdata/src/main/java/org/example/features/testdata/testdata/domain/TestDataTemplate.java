package org.example.features.testdata.testdata.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entity representing a template for generating test data
 */
@Entity
@Table(name = "test_data_templates")
public class TestDataTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String templateId;

    @Column(nullable = false)
    private String name;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private String dataType; // JSON, XML, FORM, CSV, DATABASE, API_RESPONSE

    @Column(nullable = false)
    @Lob
    private String templateStructure;

    @ElementCollection
    private List<String> fieldDefinitions = new ArrayList<>();

    @ElementCollection
    private List<String> requiredFields = new ArrayList<>();

    @ElementCollection
    private List<String> optionalFields = new ArrayList<>();

    @Column(length = 2000)
    private String validationRules;

    @Column(length = 2000)
    private String transformationRules;

    private Integer usageCount = 0;

    @Enumerated(EnumType.STRING)
    private TemplateStatus status = TemplateStatus.ACTIVE;

    private String version = "1.0";

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastUsed;

    private String createdBy;
    private String lastModifiedBy;

    @Column(length = 2000)
    private String notes;

    // Constructors
    public TestDataTemplate() {
        this.templateId = "TDT_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.fieldDefinitions = new ArrayList<>();
        this.requiredFields = new ArrayList<>();
        this.optionalFields = new ArrayList<>();
    }

    public TestDataTemplate(String name, String dataType, String templateStructure) {
        this();
        this.name = name;
        this.dataType = dataType;
        this.templateStructure = templateStructure;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTemplateId() { return templateId; }
    public void setTemplateId(String templateId) { this.templateId = templateId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDataType() { return dataType; }
    public void setDataType(String dataType) { this.dataType = dataType; }

    public String getTemplateStructure() { return templateStructure; }
    public void setTemplateStructure(String templateStructure) { this.templateStructure = templateStructure; }

    public List<String> getFieldDefinitions() { return fieldDefinitions; }
    public void setFieldDefinitions(List<String> fieldDefinitions) { this.fieldDefinitions = fieldDefinitions; }

    public List<String> getRequiredFields() { return requiredFields; }
    public void setRequiredFields(List<String> requiredFields) { this.requiredFields = requiredFields; }

    public List<String> getOptionalFields() { return optionalFields; }
    public void setOptionalFields(List<String> optionalFields) { this.optionalFields = optionalFields; }

    public String getValidationRules() { return validationRules; }
    public void setValidationRules(String validationRules) { this.validationRules = validationRules; }

    public String getTransformationRules() { return transformationRules; }
    public void setTransformationRules(String transformationRules) { this.transformationRules = transformationRules; }

    public Integer getUsageCount() { return usageCount; }
    public void setUsageCount(Integer usageCount) { this.usageCount = usageCount; }

    public TemplateStatus getStatus() { return status; }
    public void setStatus(TemplateStatus status) { this.status = status; }

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
    public void incrementUsage() {
        this.usageCount++;
        this.lastUsed = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void addRequiredField(String field) {
        if (field != null && !requiredFields.contains(field)) {
            requiredFields.add(field);
        }
    }

    public void addOptionalField(String field) {
        if (field != null && !optionalFields.contains(field)) {
            optionalFields.add(field);
        }
    }

    public void addFieldDefinition(String definition) {
        if (definition != null && !fieldDefinitions.contains(definition)) {
            fieldDefinitions.add(definition);
        }
    }

    public void deactivate() {
        this.status = TemplateStatus.INACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isActive() {
        return status == TemplateStatus.ACTIVE;
    }

    // Enums
    public enum TemplateStatus {
        ACTIVE,
        INACTIVE,
        DEPRECATED,
        ARCHIVED
    }
}