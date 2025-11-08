package org.example.domain.model.testdata.valueobjects;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Value object representing the scope/context in which data is used
 */
public final class ContextScope {
    private final String scopeType;
    private final String scopeId;
    private final String scopeName;
    private final Map<String, Object> parameters;
    private final LocalDateTime validFrom;
    private final LocalDateTime validTo;
    private final boolean isDynamic;
    
    private ContextScope(Builder builder) {
        this.scopeType = builder.scopeType;
        this.scopeId = builder.scopeId;
        this.scopeName = builder.scopeName;
        this.parameters = new HashMap<>(builder.parameters);
        this.validFrom = builder.validFrom;
        this.validTo = builder.validTo;
        this.isDynamic = builder.isDynamic;
    }
    
    /**
     * Creates a global scope
     */
    public static ContextScope global() {
        return new Builder()
                .withScopeType("global")
                .withScopeId("global")
                .withScopeName("Global Context")
                .build();
    }
    
    /**
     * Creates a project scope
     */
    public static ContextScope project(String projectId, String projectName) {
        return new Builder()
                .withScopeType("project")
                .withScopeId(projectId)
                .withScopeName(projectName)
                .build();
    }
    
    /**
     * Creates a module scope
     */
    public static ContextScope module(String moduleId, String moduleName) {
        return new Builder()
                .withScopeType("module")
                .withScopeId(moduleId)
                .withScopeName(moduleName)
                .build();
    }
    
    /**
     * Creates a test scenario scope
     */
    public static ContextScope scenario(String scenarioId, String scenarioName) {
        return new Builder()
                .withScopeType("scenario")
                .withScopeId(scenarioId)
                .withScopeName(scenarioName)
                .build();
    }
    
    /**
     * Creates a test execution scope
     */
    public static ContextScope execution(String executionId) {
        return new Builder()
                .withScopeType("execution")
                .withScopeId(executionId)
                .withScopeName("Execution " + executionId)
                .withDynamic(true)
                .build();
    }
    
    /**
     * Creates a custom scope
     */
    public static ContextScope custom(String type, String id, String name) {
        return new Builder()
                .withScopeType(type)
                .withScopeId(id)
                .withScopeName(name)
                .build();
    }
    
    public String getScopeType() {
        return scopeType;
    }
    
    public String getScopeId() {
        return scopeId;
    }
    
    public String getScopeName() {
        return scopeName;
    }
    
    public Map<String, Object> getParameters() {
        return new HashMap<>(parameters);
    }
    
    public LocalDateTime getValidFrom() {
        return validFrom;
    }
    
    public LocalDateTime getValidTo() {
        return validTo;
    }
    
    public boolean isDynamic() {
        return isDynamic;
    }
    
    /**
     * Adds a parameter to the context scope
     */
    public ContextScope withParameter(String key, Object value) {
        Builder builder = new Builder(this);
        builder.withParameter(key, value);
        return builder.build();
    }
    
    /**
     * Sets validity period
     */
    public ContextScope withValidity(LocalDateTime from, LocalDateTime to) {
        Builder builder = new Builder(this);
        builder.withValidity(from, to);
        return builder.build();
    }
    
    /**
     * Checks if the scope is currently valid
     */
    public boolean isValid() {
        LocalDateTime now = LocalDateTime.now();
        if (validFrom != null && now.isBefore(validFrom)) {
            return false;
        }
        if (validTo != null && now.isAfter(validTo)) {
            return false;
        }
        return true;
    }
    
    /**
     * Checks if this scope is a sub-scope of another scope
     */
    public boolean isSubScopeOf(ContextScope parent) {
        if (parent == null) {
            return false;
        }
        
        // Global scope contains all others
        if ("global".equals(parent.scopeType)) {
            return true;
        }
        
        // Same type scopes with same ID are equal
        if (scopeType.equals(parent.scopeType) && scopeId.equals(parent.scopeId)) {
            return true;
        }
        
        // Hierarchical containment rules
        Map<String, String> hierarchy = new HashMap<>();
        hierarchy.put("global", ""); // Global contains all
        hierarchy.put("project", "global");
        hierarchy.put("module", "project");
        hierarchy.put("scenario", "module");
        hierarchy.put("test_case", "scenario");
        hierarchy.put("execution", "test_case");
        
        String parentType = hierarchy.get(parent.scopeType);
        return parentType != null && parentType.equals(scopeType);
    }
    
    /**
     * Checks if this scope can contain another scope
     */
    public boolean canContain(ContextScope child) {
        if (child == null) {
            return false;
        }
        return child.isSubScopeOf(this);
    }
    
    /**
     * Gets a parameter value
     */
    public Object getParameter(String key) {
        return parameters.get(key);
    }
    
    /**
     * Checks if a parameter exists
     */
    public boolean hasParameter(String key) {
        return parameters.containsKey(key);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ContextScope that = (ContextScope) obj;
        return Objects.equals(scopeType, that.scopeType) &&
                Objects.equals(scopeId, that.scopeId) &&
                Objects.equals(scopeName, that.scopeName);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(scopeType, scopeId, scopeName);
    }
    
    @Override
    public String toString() {
        return "ContextScope{" +
                "type='" + scopeType + '\'' +
                ", id='" + scopeId + '\'' +
                ", name='" + scopeName + '\'' +
                '}';
    }
    
    /**
     * Builder for ContextScope
     */
    public static class Builder {
        private String scopeType;
        private String scopeId;
        private String scopeName;
        private Map<String, Object> parameters = new HashMap<>();
        private LocalDateTime validFrom;
        private LocalDateTime validTo;
        private boolean isDynamic = false;
        
        public Builder() {}
        
        private Builder(ContextScope scope) {
            this.scopeType = scope.scopeType;
            this.scopeId = scope.scopeId;
            this.scopeName = scope.scopeName;
            this.parameters = new HashMap<>(scope.parameters);
            this.validFrom = scope.validFrom;
            this.validTo = scope.validTo;
            this.isDynamic = scope.isDynamic;
        }
        
        public Builder withScopeType(String scopeType) {
            this.scopeType = scopeType;
            return this;
        }
        
        public Builder withScopeId(String scopeId) {
            this.scopeId = scopeId;
            return this;
        }
        
        public Builder withScopeName(String scopeName) {
            this.scopeName = scopeName;
            return this;
        }
        
        public Builder withParameter(String key, Object value) {
            this.parameters.put(key, value);
            return this;
        }
        
        public Builder withParameters(Map<String, Object> parameters) {
            this.parameters.putAll(parameters);
            return this;
        }
        
        public Builder withValidity(LocalDateTime from, LocalDateTime to) {
            this.validFrom = from;
            this.validTo = to;
            return this;
        }
        
        public Builder withDynamic(boolean dynamic) {
            this.isDynamic = dynamic;
            return this;
        }
        
        public ContextScope build() {
            if (scopeType == null || scopeType.trim().isEmpty()) {
                throw new IllegalArgumentException("Scope type cannot be null or empty");
            }
            if (scopeId == null || scopeId.trim().isEmpty()) {
                throw new IllegalArgumentException("Scope ID cannot be null or empty");
            }
            if (scopeName == null || scopeName.trim().isEmpty()) {
                throw new IllegalArgumentException("Scope name cannot be null or empty");
            }
            return new ContextScope(this);
        }
    }
}