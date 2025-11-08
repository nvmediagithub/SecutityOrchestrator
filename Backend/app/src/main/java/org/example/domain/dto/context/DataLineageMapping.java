package org.example.domain.dto.context;

import java.util.List;
import java.util.Map;

/**
 * DTO representing data lineage mapping between systems
 */
public class DataLineageMapping {
    private String mappingId;
    private String sourceSystem;
    private String targetSystem;
    private String dataEntity;
    private String transformation;
    private String mappingType;
    private String status;
    private List<String> fields;
    private Map<String, String> fieldMappings;
    private String dataFlow;
    private String businessRule;
    private String validation;
    private Double qualityScore;
    private List<String> dependencies;

    public DataLineageMapping() {}

    public DataLineageMapping(String mappingId, String sourceSystem, String targetSystem) {
        this.mappingId = mappingId;
        this.sourceSystem = sourceSystem;
        this.targetSystem = targetSystem;
    }

    // Getters and Setters
    public String getMappingId() { return mappingId; }
    public void setMappingId(String mappingId) { this.mappingId = mappingId; }

    public String getSourceSystem() { return sourceSystem; }
    public void setSourceSystem(String sourceSystem) { this.sourceSystem = sourceSystem; }

    public String getTargetSystem() { return targetSystem; }
    public void setTargetSystem(String targetSystem) { this.targetSystem = targetSystem; }

    public String getDataEntity() { return dataEntity; }
    public void setDataEntity(String dataEntity) { this.dataEntity = dataEntity; }

    public String getTransformation() { return transformation; }
    public void setTransformation(String transformation) { this.transformation = transformation; }

    public String getMappingType() { return mappingType; }
    public void setMappingType(String mappingType) { this.mappingType = mappingType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<String> getFields() { return fields; }
    public void setFields(List<String> fields) { this.fields = fields; }

    public Map<String, String> getFieldMappings() { return fieldMappings; }
    public void setFieldMappings(Map<String, String> fieldMappings) { this.fieldMappings = fieldMappings; }

    public String getDataFlow() { return dataFlow; }
    public void setDataFlow(String dataFlow) { this.dataFlow = dataFlow; }

    public String getBusinessRule() { return businessRule; }
    public void setBusinessRule(String businessRule) { this.businessRule = businessRule; }

    public String getValidation() { return validation; }
    public void setValidation(String validation) { this.validation = validation; }

    public Double getQualityScore() { return qualityScore; }
    public void setQualityScore(Double qualityScore) { this.qualityScore = qualityScore; }

    public List<String> getDependencies() { return dependencies; }
    public void setDependencies(List<String> dependencies) { this.dependencies = dependencies; }
}