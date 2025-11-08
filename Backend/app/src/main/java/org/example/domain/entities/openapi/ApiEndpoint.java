package org.example.domain.entities.openapi;

import org.example.domain.valueobjects.HttpMethod;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/**
 * Представитель API эндпоинта с его характеристиками
 */
public class ApiEndpoint {
    
    private String path;
    private HttpMethod method;
    private String summary;
    private String description;
    private List<String> tags;
    private String operationId;
    private List<ApiParameter> parameters;
    private Map<String, Object> requestBody;
    private Map<String, Object> responses;
    private List<String> security;
    private Map<String, Object> extensions;
    private boolean deprecated;
    private List<String> servers;
    
    public ApiEndpoint() {
        this.parameters = new ArrayList<>();
        this.tags = new ArrayList<>();
        this.security = new ArrayList<>();
        this.servers = new ArrayList<>();
        this.extensions = new java.util.HashMap<>();
    }
    
    public ApiEndpoint(String path, HttpMethod method) {
        this();
        this.path = path;
        this.method = method;
    }
    
    public void addParameter(ApiParameter parameter) {
        this.parameters.add(parameter);
    }
    
    public void addTag(String tag) {
        this.tags.add(tag);
    }
    
    // Getters and Setters
    public String getPath() {
        return path;
    }
    
    public void setPath(String path) {
        this.path = path;
    }
    
    public HttpMethod getMethod() {
        return method;
    }
    
    public void setMethod(HttpMethod method) {
        this.method = method;
    }
    
    public String getSummary() {
        return summary;
    }
    
    public void setSummary(String summary) {
        this.summary = summary;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public List<String> getTags() {
        return tags;
    }
    
    public void setTags(List<String> tags) {
        this.tags = tags;
    }
    
    public String getOperationId() {
        return operationId;
    }
    
    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }
    
    public List<ApiParameter> getParameters() {
        return parameters;
    }
    
    public void setParameters(List<ApiParameter> parameters) {
        this.parameters = parameters;
    }
    
    public Map<String, Object> getRequestBody() {
        return requestBody;
    }
    
    public void setRequestBody(Map<String, Object> requestBody) {
        this.requestBody = requestBody;
    }
    
    public Map<String, Object> getResponses() {
        return responses;
    }
    
    public void setResponses(Map<String, Object> responses) {
        this.responses = responses;
    }
    
    public List<String> getSecurity() {
        return security;
    }
    
    public void setSecurity(List<String> security) {
        this.security = security;
    }
    
    public Map<String, Object> getExtensions() {
        return extensions;
    }
    
    public void setExtensions(Map<String, Object> extensions) {
        this.extensions = extensions;
    }
    
    public boolean isDeprecated() {
        return deprecated;
    }
    
    public void setDeprecated(boolean deprecated) {
        this.deprecated = deprecated;
    }
    
    public List<String> getServers() {
        return servers;
    }
    
    public void setServers(List<String> servers) {
        this.servers = servers;
    }
    
    @Override
    public String toString() {
        return "ApiEndpoint{" +
                "path='" + path + '\'' +
                ", method=" + method +
                ", summary='" + summary + '\'' +
                ", operationId='" + operationId + '\'' +
                '}';
    }
}