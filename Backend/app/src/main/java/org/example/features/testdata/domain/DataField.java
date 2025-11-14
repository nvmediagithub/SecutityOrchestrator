package org.example.features.testdata.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing a field definition for test data generation
 */
@Entity
@Table(name = "data_fields")
public class DataField {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String fieldId;

    @Column(nullable = false)
    private String fieldName;

    @Column(nullable = false)
    private String dataType; // STRING, INTEGER, DECIMAL, BOOLEAN, DATE, EMAIL, etc.

    @Column(length = 2000)
    private String description;

    private Integer minLength;
    private Integer maxLength;

    private String minValue;
    private String maxValue;

    private String pattern; // Regex pattern for validation

    @Column(length = 2000)
    private String exampleValues; // JSON array of example values

    private Boolean nullable = true;

    @Column(length = 1000)
    private String defaultValue;

    @Column(length = 2000)
    private String constraints; // Additional validation rules

    @Enumerated(EnumType.STRING)
    private FieldStatus status = FieldStatus.ACTIVE;

    private String version = "1.0";

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String createdBy;
    private String lastModifiedBy;

    // Constructors
    public DataField() {
        this.fieldId = "DF_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public DataField(String fieldName, String dataType) {
        this();
        this.fieldName = fieldName;
        this.dataType = dataType;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFieldId() { return fieldId; }
    public void setFieldId(String fieldId) { this.fieldId = fieldId; }

    public String getFieldName() { return fieldName; }
    public void setFieldName(String fieldName) { this.fieldName = fieldName; }

    public String getDataType() { return dataType; }
    public void setDataType(String dataType) { this.dataType = dataType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getMinLength() { return minLength; }
    public void setMinLength(Integer minLength) { this.minLength = minLength; }

    public Integer getMaxLength() { return maxLength; }
    public void setMaxLength(Integer maxLength) { this.maxLength = maxLength; }

    public String getMinValue() { return minValue; }
    public void setMinValue(String minValue) { this.minValue = minValue; }

    public String getMaxValue() { return maxValue; }
    public void setMaxValue(String maxValue) { this.maxValue = maxValue; }

    public String getPattern() { return pattern; }
    public void setPattern(String pattern) { this.pattern = pattern; }

    public String getExampleValues() { return exampleValues; }
    public void setExampleValues(String exampleValues) { this.exampleValues = exampleValues; }

    public Boolean getNullable() { return nullable; }
    public void setNullable(Boolean nullable) { this.nullable = nullable; }

    public String getDefaultValue() { return defaultValue; }
    public void setDefaultValue(String defaultValue) { this.defaultValue = defaultValue; }

    public String getConstraints() { return constraints; }
    public void setConstraints(String constraints) { this.constraints = constraints; }

    public FieldStatus getStatus() { return status; }
    public void setStatus(FieldStatus status) { this.status = status; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public String getLastModifiedBy() { return lastModifiedBy; }
    public void setLastModifiedBy(String lastModifiedBy) { this.lastModifiedBy = lastModifiedBy; }

    // Helper methods
    public boolean isActive() {
        return status == FieldStatus.ACTIVE;
    }

    public boolean isValidLength(int length) {
        if (minLength != null && length < minLength) return false;
        if (maxLength != null && length > maxLength) return false;
        return true;
    }

    public boolean isValidValue(String value) {
        if (value == null) return nullable;
        if (pattern != null && !value.matches(pattern)) return false;
        // Additional validation logic can be added here
        return true;
    }

    public void deactivate() {
        this.status = FieldStatus.INACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    // Enums
    public enum FieldStatus {
        ACTIVE,
        INACTIVE,
        DEPRECATED
    }
}