package org.example.domain.entities.openapi;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.example.domain.valueobjects.OpenApiVersion;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Сущность для хранения информации о сервисе с OpenAPI спецификацией
 */
@Entity
@Table(name = "openapi_services")
@EntityListeners(AuditingEntityListener.class)
public class OpenApiService {
    
    @Id
    @GeneratedValue
    private UUID id;
    
    @NotBlank
    @Column(unique = true, nullable = false)
    private String serviceName;
    
    @NotBlank
    @Column(nullable = false)
    private String serviceTitle;
    
    private String serviceVersion;
    
    private String description;
    
    private String baseUrl;
    
    @NotBlank
    @Column(name = "specification_url", nullable = false)
    private String specificationUrl;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "openapi_version", nullable = false)
    private OpenApiVersion openApiVersion;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "last_parsed_at")
    private LocalDateTime lastParsedAt;
    
    @Column(name = "parse_status")
    @Enumerated(EnumType.STRING)
    private ParseStatus parseStatus = ParseStatus.PENDING;
    
    private String tags;
    
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Связи
    @ElementCollection
    @CollectionTable(name = "openapi_service_endpoints", joinColumns = @JoinColumn(name = "service_id"))
    private List<ApiEndpoint> endpoints = new ArrayList<>();
    
    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ApiSchema> schemas = new ArrayList<>();
    
    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EndpointAnalysis> analyses = new ArrayList<>();
    
    // Конструкторы
    public OpenApiService() {}
    
    public OpenApiService(String serviceName, String serviceTitle, String specificationUrl, OpenApiVersion openApiVersion) {
        this.serviceName = serviceName;
        this.serviceTitle = serviceTitle;
        this.specificationUrl = specificationUrl;
        this.openApiVersion = openApiVersion;
    }
    
    // Геттеры и сеттеры
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }
    
    public String getServiceTitle() { return serviceTitle; }
    public void setServiceTitle(String serviceTitle) { this.serviceTitle = serviceTitle; }
    
    public String getServiceVersion() { return serviceVersion; }
    public void setServiceVersion(String serviceVersion) { this.serviceVersion = serviceVersion; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
    
    public String getSpecificationUrl() { return specificationUrl; }
    public void setSpecificationUrl(String specificationUrl) { this.specificationUrl = specificationUrl; }
    
    public OpenApiVersion getOpenApiVersion() { return openApiVersion; }
    public void setOpenApiVersion(OpenApiVersion openApiVersion) { this.openApiVersion = openApiVersion; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public LocalDateTime getLastParsedAt() { return lastParsedAt; }
    public void setLastParsedAt(LocalDateTime lastParsedAt) { this.lastParsedAt = lastParsedAt; }
    
    public ParseStatus getParseStatus() { return parseStatus; }
    public void setParseStatus(ParseStatus parseStatus) { this.parseStatus = parseStatus; }
    
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public List<ApiEndpoint> getEndpoints() { return endpoints; }
    public void setEndpoints(List<ApiEndpoint> endpoints) { this.endpoints = endpoints; }
    
    public List<ApiSchema> getSchemas() { return schemas; }
    public void setSchemas(List<ApiSchema> schemas) { this.schemas = schemas; }
    
    public List<EndpointAnalysis> getAnalyses() { return analyses; }
    public void setAnalyses(List<EndpointAnalysis> analyses) { this.analyses = analyses; }
    
    // Вспомогательные методы
    public boolean isParsed() {
        return parseStatus == ParseStatus.SUCCESS;
    }
    
    public boolean hasError() {
        return parseStatus == ParseStatus.ERROR;
    }
    
    public void addEndpoint(ApiEndpoint endpoint) {
        endpoints.add(endpoint);
    }
    
    public void removeEndpoint(ApiEndpoint endpoint) {
        endpoints.remove(endpoint);
    }
    
    public void addSchema(ApiSchema schema) {
        schemas.add(schema);
    }
    
    public void removeSchema(ApiSchema schema) {
        schemas.remove(schema);
    }
    
    public void addAnalysis(EndpointAnalysis analysis) {
        analyses.add(analysis);
    }
    
    public int getEndpointCount() {
        return endpoints.size();
    }
    
    public int getSchemaCount() {
        return schemas.size();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OpenApiService that = (OpenApiService) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "OpenApiService{" +
                "id=" + id +
                ", serviceName='" + serviceName + '\'' +
                ", serviceTitle='" + serviceTitle + '\'' +
                ", serviceVersion='" + serviceVersion + '\'' +
                ", parseStatus=" + parseStatus +
                '}';
    }
    
    // Перечисления
    public enum ParseStatus {
        PENDING, PROCESSING, SUCCESS, ERROR
    }
}