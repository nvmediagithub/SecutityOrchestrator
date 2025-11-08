package org.example.domain.entities.openapi;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Сущность для хранения схем данных API
 */
@Entity
@Table(name = "api_schemas")
@EntityListeners(AuditingEntityListener.class)
public class ApiSchema {
    
    @Id
    @GeneratedValue
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id")
    private OpenApiService service;
    
    @NotBlank
    private String name;
    
    private String type;
    
    private String format;
    
    private String description;
    
    private String title;
    
    @Column(name = "is_abstract")
    private Boolean isAbstract = false;
    
    @Column(name = "is_deprecated")
    private Boolean isDeprecated = false;
    
    @Column(name = "example_value")
    private String exampleValue;
    
    @ElementCollection
    @CollectionTable(name = "schema_required_fields", joinColumns = @JoinColumn(name = "schema_id"))
    @Column(name = "required_field")
    private List<String> requiredFields = new ArrayList<>();
    
    @ElementCollection
    @CollectionTable(name = "schema_properties", joinColumns = @JoinColumn(name = "schema_id"))
    @MapKeyColumn(name = "property_name")
    @Column(name = "property_value", columnDefinition = "CLOB")
    private Map<String, String> properties = new HashMap<>();
    
    @ElementCollection
    @CollectionTable(name = "schema_constraints", joinColumns = @JoinColumn(name = "schema_id"))
    @MapKeyColumn(name = "constraint_name")
    @Column(name = "constraint_value", columnDefinition = "CLOB")
    private Map<String, String> constraints = new HashMap<>();
    
    @ElementCollection
    @CollectionTable(name = "schema_examples", joinColumns = @JoinColumn(name = "schema_id"))
    @MapKeyColumn(name = "example_name")
    @Column(name = "example_value", columnDefinition = "CLOB")
    private Map<String, String> examples = new HashMap<>();
    
    @Column(name = "external_docs")
    private String externalDocs;
    
    @Column(name = "raw_schema", columnDefinition = "CLOB")
    private String rawSchema;
    
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Конструкторы
    public ApiSchema() {}
    
    public ApiSchema(String name, String type) {
        this.name = name;
        this.type = type;
    }
    
    public ApiSchema(String name, String type, String description) {
        this(name, type);
        this.description = description;
    }
    
    // Геттеры и сеттеры
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public OpenApiService getService() { return service; }
    public void setService(OpenApiService service) { this.service = service; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public Boolean getIsAbstract() { return isAbstract; }
    public void setIsAbstract(Boolean isAbstract) { this.isAbstract = isAbstract; }
    
    public Boolean getIsDeprecated() { return isDeprecated; }
    public void setIsDeprecated(Boolean isDeprecated) { this.isDeprecated = isDeprecated; }
    
    public String getExampleValue() { return exampleValue; }
    public void setExampleValue(String exampleValue) { this.exampleValue = exampleValue; }
    
    public List<String> getRequiredFields() { return requiredFields; }
    public void setRequiredFields(List<String> requiredFields) { this.requiredFields = requiredFields; }
    
    public Map<String, String> getProperties() { return properties; }
    public void setProperties(Map<String, String> properties) { this.properties = properties; }
    
    public Map<String, String> getConstraints() { return constraints; }
    public void setConstraints(Map<String, String> constraints) { this.constraints = constraints; }
    
    public Map<String, String> getExamples() { return examples; }
    public void setExamples(Map<String, String> examples) { this.examples = examples; }
    
    public String getExternalDocs() { return externalDocs; }
    public void setExternalDocs(String externalDocs) { this.externalDocs = externalDocs; }
    
    public String getRawSchema() { return rawSchema; }
    public void setRawSchema(String rawSchema) { this.rawSchema = rawSchema; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // Вспомогательные методы
    public void addRequiredField(String field) {
        if (!requiredFields.contains(field)) {
            requiredFields.add(field);
        }
    }
    
    public void removeRequiredField(String field) {
        requiredFields.remove(field);
    }
    
    public void addProperty(String name, String definition) {
        properties.put(name, definition);
    }
    
    public void removeProperty(String name) {
        properties.remove(name);
    }
    
    public void addConstraint(String name, String value) {
        constraints.put(name, value);
    }
    
    public void addExample(String name, String value) {
        examples.put(name, value);
    }
    
    public boolean hasProperty(String propertyName) {
        return properties.containsKey(propertyName);
    }
    
    public boolean isRequired(String field) {
        return requiredFields.contains(field);
    }
    
    public int getPropertyCount() {
        return properties.size();
    }
    
    public int getRequiredFieldCount() {
        return requiredFields.size();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApiSchema apiSchema = (ApiSchema) o;
        return Objects.equals(id, apiSchema.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "ApiSchema{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", format='" + format + '\'' +
                '}';
    }
}