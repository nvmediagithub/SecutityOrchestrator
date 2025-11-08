package org.example.domain.dto.dependency;

import java.util.List;
import java.util.Set;
import java.util.Objects;

/**
 * Represents API dependency information between endpoints
 */
public class ApiDependency {
    private String sourceEndpoint;
    private String targetEndpoint;
    private String sourceMethod;
    private String targetMethod;
    private DependencyType type;
    private Set<String> sharedFields;
    private Set<String> createdFields;
    private Set<String> consumedFields;
    private String foreignKeyRelationship;
    private String parentChildRelationship;
    private String authenticationDependency;
    private String queryParameterDependency;
    private String pathParameterDependency;
    private List<String> sequenceDependencies;
    private String masterDataReference;
    private String lookupTableReference;
    private String contextData;
    
    public enum DependencyType {
        DIRECT_RESPONSE,
        FOREIGN_KEY,
        PARENT_CHILD,
        SEQUENCE,
        AUTHENTICATION,
        QUERY_PARAM,
        PATH_PARAM,
        MASTER_DATA,
        LOOKUP_TABLE,
        CONTEXT_SHARING
    }
    
    public ApiDependency() {}
    
    public ApiDependency(String sourceEndpoint, String targetEndpoint, String sourceMethod, 
                        String targetMethod, DependencyType type) {
        this.sourceEndpoint = sourceEndpoint;
        this.targetEndpoint = targetEndpoint;
        this.sourceMethod = sourceMethod;
        this.targetMethod = targetMethod;
        this.type = type;
    }
    
    // Getters and Setters
    public String getSourceEndpoint() {
        return sourceEndpoint;
    }
    
    public void setSourceEndpoint(String sourceEndpoint) {
        this.sourceEndpoint = sourceEndpoint;
    }
    
    public String getTargetEndpoint() {
        return targetEndpoint;
    }
    
    public void setTargetEndpoint(String targetEndpoint) {
        this.targetEndpoint = targetEndpoint;
    }
    
    public String getSourceMethod() {
        return sourceMethod;
    }
    
    public void setSourceMethod(String sourceMethod) {
        this.sourceMethod = sourceMethod;
    }
    
    public String getTargetMethod() {
        return targetMethod;
    }
    
    public void setTargetMethod(String targetMethod) {
        this.targetMethod = targetMethod;
    }
    
    public DependencyType getType() {
        return type;
    }
    
    public void setType(DependencyType type) {
        this.type = type;
    }
    
    public Set<String> getSharedFields() {
        return sharedFields;
    }
    
    public void setSharedFields(Set<String> sharedFields) {
        this.sharedFields = sharedFields;
    }
    
    public Set<String> getCreatedFields() {
        return createdFields;
    }
    
    public void setCreatedFields(Set<String> createdFields) {
        this.createdFields = createdFields;
    }
    
    public Set<String> getConsumedFields() {
        return consumedFields;
    }
    
    public void setConsumedFields(Set<String> consumedFields) {
        this.consumedFields = consumedFields;
    }
    
    public String getForeignKeyRelationship() {
        return foreignKeyRelationship;
    }
    
    public void setForeignKeyRelationship(String foreignKeyRelationship) {
        this.foreignKeyRelationship = foreignKeyRelationship;
    }
    
    public String getParentChildRelationship() {
        return parentChildRelationship;
    }
    
    public void setParentChildRelationship(String parentChildRelationship) {
        this.parentChildRelationship = parentChildRelationship;
    }
    
    public String getAuthenticationDependency() {
        return authenticationDependency;
    }
    
    public void setAuthenticationDependency(String authenticationDependency) {
        this.authenticationDependency = authenticationDependency;
    }
    
    public String getQueryParameterDependency() {
        return queryParameterDependency;
    }
    
    public void setQueryParameterDependency(String queryParameterDependency) {
        this.queryParameterDependency = queryParameterDependency;
    }
    
    public String getPathParameterDependency() {
        return pathParameterDependency;
    }
    
    public void setPathParameterDependency(String pathParameterDependency) {
        this.pathParameterDependency = pathParameterDependency;
    }
    
    public List<String> getSequenceDependencies() {
        return sequenceDependencies;
    }
    
    public void setSequenceDependencies(List<String> sequenceDependencies) {
        this.sequenceDependencies = sequenceDependencies;
    }
    
    public String getMasterDataReference() {
        return masterDataReference;
    }
    
    public void setMasterDataReference(String masterDataReference) {
        this.masterDataReference = masterDataReference;
    }
    
    public String getLookupTableReference() {
        return lookupTableReference;
    }
    
    public void setLookupTableReference(String lookupTableReference) {
        this.lookupTableReference = lookupTableReference;
    }
    
    public String getContextData() {
        return contextData;
    }
    
    public void setContextData(String contextData) {
        this.contextData = contextData;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApiDependency that = (ApiDependency) o;
        return Objects.equals(sourceEndpoint, that.sourceEndpoint) &&
                Objects.equals(targetEndpoint, that.targetEndpoint) &&
                Objects.equals(sourceMethod, that.sourceMethod) &&
                Objects.equals(targetMethod, that.targetMethod) &&
                type == that.type;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(sourceEndpoint, targetEndpoint, sourceMethod, targetMethod, type);
    }
    
    @Override
    public String toString() {
        return "ApiDependency{" +
                "sourceEndpoint='" + sourceEndpoint + '\'' +
                ", targetEndpoint='" + targetEndpoint + '\'' +
                ", sourceMethod='" + sourceMethod + '\'' +
                ", targetMethod='" + targetMethod + '\'' +
                ", type=" + type +
                '}';
    }
}