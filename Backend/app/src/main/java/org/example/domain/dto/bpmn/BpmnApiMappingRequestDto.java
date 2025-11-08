package org.example.domain.dto.bpmn;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * Request DTO for BPMN to API mapping operations
 * Used for POST /api/analysis/bpmn/{diagramId}/map-api endpoint
 */
public class BpmnApiMappingRequestDto {
    
    @NotBlank(message = "Diagram ID is required")
    private String diagramId;
    
    @NotBlank(message = "API specification ID is required")
    private String apiSpecId;
    
    @NotEmpty(message = "At least one mapping is required")
    private List<BpmnApiMapping> mappings;
    
    private Map<String, Object> mappingOptions;
    private String mappingStrategy; // "auto", "manual", "hybrid"
    private boolean validateMappings;
    private String confidenceThreshold;
    
    public BpmnApiMappingRequestDto() {}
    
    public BpmnApiMappingRequestDto(String diagramId, String apiSpecId, List<BpmnApiMapping> mappings,
                                   Map<String, Object> mappingOptions, String mappingStrategy,
                                   boolean validateMappings, String confidenceThreshold) {
        this.diagramId = diagramId;
        this.apiSpecId = apiSpecId;
        this.mappings = mappings;
        this.mappingOptions = mappingOptions;
        this.mappingStrategy = mappingStrategy;
        this.validateMappings = validateMappings;
        this.confidenceThreshold = confidenceThreshold;
    }
    
    public static class BpmnApiMapping {
        @NotBlank(message = "BPMN element ID is required")
        private String bpmnElementId;
        
        @NotBlank(message = "BPMN element name is required")
        private String bpmnElementName;
        
        @NotBlank(message = "BPMN element type is required")
        private String bpmnElementType;
        
        @NotBlank(message = "API endpoint is required")
        private String apiEndpoint;
        
        @NotBlank(message = "HTTP method is required")
        private String httpMethod;
        
        private String apiOperationId;
        private String mappingType; // "direct", "transformation", "aggregation"
        private Double confidence;
        private Map<String, Object> transformationRules;
        private String description;
        private boolean bidirectional;
        
        public BpmnApiMapping() {}
        
        public BpmnApiMapping(String bpmnElementId, String bpmnElementName, String bpmnElementType,
                            String apiEndpoint, String httpMethod, String apiOperationId,
                            String mappingType, Double confidence, Map<String, Object> transformationRules,
                            String description, boolean bidirectional) {
            this.bpmnElementId = bpmnElementId;
            this.bpmnElementName = bpmnElementName;
            this.bpmnElementType = bpmnElementType;
            this.apiEndpoint = apiEndpoint;
            this.httpMethod = httpMethod;
            this.apiOperationId = apiOperationId;
            this.mappingType = mappingType;
            this.confidence = confidence;
            this.transformationRules = transformationRules;
            this.description = description;
            this.bidirectional = bidirectional;
        }
        
        // Getters and setters
        public String getBpmnElementId() { return bpmnElementId; }
        public void setBpmnElementId(String bpmnElementId) { this.bpmnElementId = bpmnElementId; }
        
        public String getBpmnElementName() { return bpmnElementName; }
        public void setBpmnElementName(String bpmnElementName) { this.bpmnElementName = bpmnElementName; }
        
        public String getBpmnElementType() { return bpmnElementType; }
        public void setBpmnElementType(String bpmnElementType) { this.bpmnElementType = bpmnElementType; }
        
        public String getApiEndpoint() { return apiEndpoint; }
        public void setApiEndpoint(String apiEndpoint) { this.apiEndpoint = apiEndpoint; }
        
        public String getHttpMethod() { return httpMethod; }
        public void setHttpMethod(String httpMethod) { this.httpMethod = httpMethod; }
        
        public String getApiOperationId() { return apiOperationId; }
        public void setApiOperationId(String apiOperationId) { this.apiOperationId = apiOperationId; }
        
        public String getMappingType() { return mappingType; }
        public void setMappingType(String mappingType) { this.mappingType = mappingType; }
        
        public Double getConfidence() { return confidence; }
        public void setConfidence(Double confidence) { this.confidence = confidence; }
        
        public Map<String, Object> getTransformationRules() { return transformationRules; }
        public void setTransformationRules(Map<String, Object> transformationRules) { this.transformationRules = transformationRules; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public boolean isBidirectional() { return bidirectional; }
        public void setBidirectional(boolean bidirectional) { this.bidirectional = bidirectional; }
        
        // Utility methods
        public boolean isHighConfidence() {
            return confidence != null && confidence >= 0.8;
        }
        
        public boolean isTransformationMapping() {
            return "transformation".equalsIgnoreCase(mappingType);
        }
        
        public boolean isAggregationMapping() {
            return "aggregation".equalsIgnoreCase(mappingType);
        }
    }
    
    // Validation method
    public boolean isValid() {
        return diagramId != null && !diagramId.trim().isEmpty() &&
               apiSpecId != null && !apiSpecId.trim().isEmpty() &&
               mappings != null && !mappings.isEmpty() &&
               mappings.stream().allMatch(mapping -> 
                   mapping.getBpmnElementId() != null && !mapping.getBpmnElementId().trim().isEmpty() &&
                   mapping.getApiEndpoint() != null && !mapping.getApiEndpoint().trim().isEmpty() &&
                   mapping.getHttpMethod() != null && !mapping.getHttpMethod().trim().isEmpty());
    }
    
    // Getters and setters
    public String getDiagramId() { return diagramId; }
    public void setDiagramId(String diagramId) { this.diagramId = diagramId; }
    
    public String getApiSpecId() { return apiSpecId; }
    public void setApiSpecId(String apiSpecId) { this.apiSpecId = apiSpecId; }
    
    public List<BpmnApiMapping> getMappings() { return mappings; }
    public void setMappings(List<BpmnApiMapping> mappings) { this.mappings = mappings; }
    
    public Map<String, Object> getMappingOptions() { return mappingOptions; }
    public void setMappingOptions(Map<String, Object> mappingOptions) { this.mappingOptions = mappingOptions; }
    
    public String getMappingStrategy() { return mappingStrategy; }
    public void setMappingStrategy(String mappingStrategy) { this.mappingStrategy = mappingStrategy; }
    
    public boolean isValidateMappings() { return validateMappings; }
    public void setValidateMappings(boolean validateMappings) { this.validateMappings = validateMappings; }
    
    public String getConfidenceThreshold() { return confidenceThreshold; }
    public void setConfidenceThreshold(String confidenceThreshold) { this.confidenceThreshold = confidenceThreshold; }
}