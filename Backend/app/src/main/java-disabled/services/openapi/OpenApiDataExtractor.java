package org.example.infrastructure.services.openapi;

import org.example.domain.entities.openapi.OpenApiSpecification;
import org.example.domain.entities.openapi.ApiEndpoint;
import org.example.domain.entities.openapi.ApiSchema;
import org.example.domain.entities.openapi.ApiSecurityScheme;
import org.example.domain.valueobjects.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Извлекатель данных из OpenAPI спецификаций
 */
@Service
public class OpenApiDataExtractor {
    
    public OpenApiLLMData prepareForLLMAnalysis(OpenApiSpecification specification) {
        OpenApiLLMData data = new OpenApiLLMData();
        
        data.setTitle(specification.getTitle());
        data.setDescription(specification.getDescription());
        data.setVersion(specification.getVersion() != null ? 
                       specification.getVersion().toString() : "1.0.0");
        data.setOpenApiVersion(specification.getOpenApiVersion().toString());
        
        data.setEndpoints(extractEndpointsSummary(specification.getEndpoints()));
        data.setSchemas(extractSchemasSummary(specification.getSchemas()));
        data.setSecuritySchemes(extractSecuritySchemesSummary(specification.getSecuritySchemes()));
        data.setServers(extractServersSummary(specification.getServers()));
        data.setTags(extractTagsSummary(specification.getTags()));
        data.setStatistics(generateStatistics(specification));
        
        return data;
    }
    
    public String generateSummary(OpenApiSpecification specification) {
        StringBuilder summary = new StringBuilder();
        
        summary.append("# ").append(specification.getTitle()).append("\n\n");
        
        if (specification.getDescription() != null) {
            summary.append(specification.getDescription()).append("\n\n");
        }
        
        summary.append("**Версия API:** ").append(
            specification.getVersion() != null ? 
            specification.getVersion().toString() : "1.0.0"
        ).append("\n\n");
        
        summary.append("**Версия OpenAPI:** ").append(
            specification.getOpenApiVersion().toString()
        ).append("\n\n");
        
        if (specification.getEndpoints() != null) {
            Map<HttpMethod, Long> methodStats = specification.getEndpoints().stream()
                .collect(Collectors.groupingBy(
                    ApiEndpoint::getMethod, 
                    Collectors.counting()
                ));
            
            summary.append("## Статистика эндпоинтов\n\n");
            for (Map.Entry<HttpMethod, Long> entry : methodStats.entrySet()) {
                summary.append("- ").append(entry.getKey())
                       .append(": ").append(entry.getValue()).append(" эндпоинтов\n");
            }
            summary.append("\n");
        }
        
        return summary.toString();
    }
    
    private List<EndpointSummary> extractEndpointsSummary(List<ApiEndpoint> endpoints) {
        if (endpoints == null) return new ArrayList<>();
        return endpoints.stream().map(this::createEndpointSummary).collect(Collectors.toList());
    }
    
    private EndpointSummary createEndpointSummary(ApiEndpoint endpoint) {
        EndpointSummary summary = new EndpointSummary();
        summary.setMethod(endpoint.getMethod().toString());
        summary.setPath(endpoint.getPath());
        summary.setSummary(endpoint.getSummary());
        summary.setDescription(endpoint.getDescription());
        summary.setOperationId(endpoint.getOperationId());
        if (endpoint.getTags() != null) {
            summary.setTags(new ArrayList<>(endpoint.getTags()));
        }
        return summary;
    }
    
    private List<SchemaSummary> extractSchemasSummary(List<ApiSchema> schemas) {
        if (schemas == null) return new ArrayList<>();
        return schemas.stream().map(this::createSchemaSummary).collect(Collectors.toList());
    }
    
    private SchemaSummary createSchemaSummary(ApiSchema schema) {
        SchemaSummary summary = new SchemaSummary();
        summary.setName(schema.getName());
        summary.setType(schema.getType());
        summary.setDescription(schema.getDescription());
        if (schema.getProperties() != null) {
            summary.setPropertyCount(schema.getProperties().size());
        }
        return summary;
    }
    
    private List<SecuritySchemeSummary> extractSecuritySchemesSummary(List<ApiSecurityScheme> schemes) {
        if (schemes == null) return new ArrayList<>();
        return schemes.stream().map(this::createSecuritySchemeSummary).collect(Collectors.toList());
    }
    
    private SecuritySchemeSummary createSecuritySchemeSummary(ApiSecurityScheme scheme) {
        SecuritySchemeSummary summary = new SecuritySchemeSummary();
        summary.setName(scheme.getName());
        summary.setType(scheme.getType());
        summary.setDescription(scheme.getDescription());
        return summary;
    }
    
    private List<String> extractServersSummary(Map<String, Object> servers) {
        if (servers == null) return new ArrayList<>();
        return new ArrayList<>(servers.keySet());
    }
    
    private List<TagSummary> extractTagsSummary(Map<String, Object> tags) {
        if (tags == null) return new ArrayList<>();
        return tags.keySet().stream()
            .map(name -> {
                TagSummary tag = new TagSummary();
                tag.setName(name);
                return tag;
            })
            .collect(Collectors.toList());
    }
    
    private OpenApiStatistics generateStatistics(OpenApiSpecification specification) {
        OpenApiStatistics stats = new OpenApiStatistics();
        
        if (specification.getEndpoints() != null) {
            stats.setTotalEndpoints(specification.getEndpoints().size());
        }
        
        if (specification.getSchemas() != null) {
            stats.setTotalSchemas(specification.getSchemas().size());
        }
        
        if (specification.getSecuritySchemes() != null) {
            stats.setTotalSecuritySchemes(specification.getSecuritySchemes().size());
        }
        
        return stats;
    }
    
    public static class OpenApiLLMData {
        private String title;
        private String description;
        private String version;
        private String openApiVersion;
        private List<EndpointSummary> endpoints;
        private List<SchemaSummary> schemas;
        private List<SecuritySchemeSummary> securitySchemes;
        private List<String> servers;
        private List<TagSummary> tags;
        private OpenApiStatistics statistics;
        
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
        public String getOpenApiVersion() { return openApiVersion; }
        public void setOpenApiVersion(String openApiVersion) { this.openApiVersion = openApiVersion; }
        public List<EndpointSummary> getEndpoints() { return endpoints; }
        public void setEndpoints(List<EndpointSummary> endpoints) { this.endpoints = endpoints; }
        public List<SchemaSummary> getSchemas() { return schemas; }
        public void setSchemas(List<SchemaSummary> schemas) { this.schemas = schemas; }
        public List<SecuritySchemeSummary> getSecuritySchemes() { return securitySchemes; }
        public void setSecuritySchemes(List<SecuritySchemeSummary> securitySchemes) { this.securitySchemes = securitySchemes; }
        public List<String> getServers() { return servers; }
        public void setServers(List<String> servers) { this.servers = servers; }
        public List<TagSummary> getTags() { return tags; }
        public void setTags(List<TagSummary> tags) { this.tags = tags; }
        public OpenApiStatistics getStatistics() { return statistics; }
        public void setStatistics(OpenApiStatistics statistics) { this.statistics = statistics; }
    }
    
    public static class EndpointSummary {
        private String method;
        private String path;
        private String summary;
        private String description;
        private String operationId;
        private List<String> tags;
        
        public String getMethod() { return method; }
        public void setMethod(String method) { this.method = method; }
        public String getPath() { return path; }
        public void setPath(String path) { this.path = path; }
        public String getSummary() { return summary; }
        public void setSummary(String summary) { this.summary = summary; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getOperationId() { return operationId; }
        public void setOperationId(String operationId) { this.operationId = operationId; }
        public List<String> getTags() { return tags; }
        public void setTags(List<String> tags) { this.tags = tags; }
    }
    
    public static class SchemaSummary {
        private String name;
        private String type;
        private String description;
        private int propertyCount;
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public int getPropertyCount() { return propertyCount; }
        public void setPropertyCount(int propertyCount) { this.propertyCount = propertyCount; }
    }
    
    public static class SecuritySchemeSummary {
        private String name;
        private String type;
        private String description;
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
    
    public static class TagSummary {
        private String name;
        private String description;
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
    
    public static class OpenApiStatistics {
        private int totalEndpoints;
        private int totalSchemas;
        private int totalSecuritySchemes;
        
        public int getTotalEndpoints() { return totalEndpoints; }
        public void setTotalEndpoints(int totalEndpoints) { this.totalEndpoints = totalEndpoints; }
        public int getTotalSchemas() { return totalSchemas; }
        public void setTotalSchemas(int totalSchemas) { this.totalSchemas = totalSchemas; }
        public int getTotalSecuritySchemes() { return totalSecuritySchemes; }
        public void setTotalSecuritySchemes(int totalSecuritySchemes) { this.totalSecuritySchemes = totalSecuritySchemes; }
    }
}