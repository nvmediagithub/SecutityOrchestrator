package org.example.domain.entities.openapi;

import java.util.Map;
import java.util.HashMap;

/**
 * Схема безопасности OpenAPI
 */
public class ApiSecurityScheme {
    
    private String name;
    private String type; // "http", "apiKey", "oauth2", "openIdConnect"
    private String description;
    private String scheme; // "basic", "bearer", "digest", etc.
    private String bearerFormat; // "JWT", "OAuth", etc.
    private String flow; // "implicit", "password", "application", "accessCode"
    private String authorizationUrl;
    private String tokenUrl;
    private String refreshUrl;
    private Map<String, Object> scopes; // For OAuth2
    
    public ApiSecurityScheme() {
        this.scopes = new HashMap<>();
    }
    
    public ApiSecurityScheme(String name, String type, String description) {
        this();
        this.name = name;
        this.type = type;
        this.description = description;
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getScheme() {
        return scheme;
    }
    
    public void setScheme(String scheme) {
        this.scheme = scheme;
    }
    
    public String getBearerFormat() {
        return bearerFormat;
    }
    
    public void setBearerFormat(String bearerFormat) {
        this.bearerFormat = bearerFormat;
    }
    
    public String getFlow() {
        return flow;
    }
    
    public void setFlow(String flow) {
        this.flow = flow;
    }
    
    public String getAuthorizationUrl() {
        return authorizationUrl;
    }
    
    public void setAuthorizationUrl(String authorizationUrl) {
        this.authorizationUrl = authorizationUrl;
    }
    
    public String getTokenUrl() {
        return tokenUrl;
    }
    
    public void setTokenUrl(String tokenUrl) {
        this.tokenUrl = tokenUrl;
    }
    
    public String getRefreshUrl() {
        return refreshUrl;
    }
    
    public void setRefreshUrl(String refreshUrl) {
        this.refreshUrl = refreshUrl;
    }
    
    public Map<String, Object> getScopes() {
        return scopes;
    }
    
    public void setScopes(Map<String, Object> scopes) {
        this.scopes = scopes;
    }
    
    public void addScope(String name, String description) {
        this.scopes.put(name, description);
    }
    
    @Override
    public String toString() {
        return "ApiSecurityScheme{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}