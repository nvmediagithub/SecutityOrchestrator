package org.example.domain.entities.openapi;

import org.example.domain.valueobjects.OpenApiVersion;
import org.example.domain.valueobjects.SpecificationId;
import org.example.domain.valueobjects.Version;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Главный представитель OpenAPI спецификации
 * Объединяет всю информацию о спецификации API
 */
public class OpenApiSpecification {
    
    private SpecificationId id;
    private String title;
    private String description;
    private Version version;
    private OpenApiVersion openApiVersion;
    private String rawContent;
    private Map<String, Object> originalSpec;
    private List<ApiEndpoint> endpoints;
    private List<ApiSchema> schemas;
    private List<ApiSecurityScheme> securitySchemes;
    private Map<String, Object> servers;
    private Map<String, Object> tags;
    private Map<String, Object> extensions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isValid;
    private List<String> validationErrors;
    
    public OpenApiSpecification() {
        this.id = new SpecificationId("temp-" + System.currentTimeMillis());
        this.version = new Version(1, 0, 0);
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.endpoints = new java.util.ArrayList<>();
        this.schemas = new java.util.ArrayList<>();
        this.securitySchemes = new java.util.ArrayList<>();
        this.servers = new HashMap<>();
        this.tags = new HashMap<>();
        this.extensions = new HashMap<>();
        this.validationErrors = new java.util.ArrayList<>();
        this.isValid = false;
    }
    
    public OpenApiSpecification(SpecificationId id, String title, OpenApiVersion openApiVersion, String rawContent) {
        this();
        this.id = id;
        this.title = title;
        this.openApiVersion = openApiVersion;
        this.rawContent = rawContent;
    }
    
    public void addEndpoint(ApiEndpoint endpoint) {
        this.endpoints.add(endpoint);
    }
    
    public void addSchema(ApiSchema schema) {
        this.schemas.add(schema);
    }
    
    public void addSecurityScheme(ApiSecurityScheme scheme) {
        this.securitySchemes.add(scheme);
    }
    
    public void addValidationError(String error) {
        this.validationErrors.add(error);
    }
    
    public void setValid(boolean valid) {
        isValid = valid;
    }
    
    // Getters and Setters
    public SpecificationId getId() {
        return id;
    }
    
    public void setId(SpecificationId id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
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
    
    public String getRawContent() {
        return rawContent;
    }
    
    public void setRawContent(String rawContent) {
        this.rawContent = rawContent;
    }
    
    public Map<String, Object> getOriginalSpec() {
        return originalSpec;
    }
    
    public void setOriginalSpec(Map<String, Object> originalSpec) {
        this.originalSpec = originalSpec;
    }
    
    public List<ApiEndpoint> getEndpoints() {
        return endpoints;
    }
    
    public void setEndpoints(List<ApiEndpoint> endpoints) {
        this.endpoints = endpoints;
    }
    
    public List<ApiSchema> getSchemas() {
        return schemas;
    }
    
    public void setSchemas(List<ApiSchema> schemas) {
        this.schemas = schemas;
    }
    
    public List<ApiSecurityScheme> getSecuritySchemes() {
        return securitySchemes;
    }
    
    public void setSecuritySchemes(List<ApiSecurityScheme> securitySchemes) {
        this.securitySchemes = securitySchemes;
    }
    
    public Map<String, Object> getServers() {
        return servers;
    }
    
    public void setServers(Map<String, Object> servers) {
        this.servers = servers;
    }
    
    public Map<String, Object> getTags() {
        return tags;
    }
    
    public void setTags(Map<String, Object> tags) {
        this.tags = tags;
    }
    
    public Map<String, Object> getExtensions() {
        return extensions;
    }
    
    public void setExtensions(Map<String, Object> extensions) {
        this.extensions = extensions;
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
    
    public boolean isValid() {
        return isValid;
    }
    
    public List<String> getValidationErrors() {
        return validationErrors;
    }
    
    public void setValidationErrors(List<String> validationErrors) {
        this.validationErrors = validationErrors;
    }
    
    @Override
    public String toString() {
        return "OpenApiSpecification{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", openApiVersion=" + openApiVersion +
                ", endpoints=" + endpoints.size() +
                ", schemas=" + schemas.size() +
                ", isValid=" + isValid +
                '}';
    }
}