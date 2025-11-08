package org.example.domain.dto.context;

import java.util.List;
import java.util.Map;

/**
 * Маппинг потоков данных
 */
public class DataFlowMapping {
    
    private String mappingId;
    private String sourceElementId;
    private String targetElementId;
    private String dataType;
    private List<String> dataFields;
    private Map<String, Object> transformationRules;
    
    public DataFlowMapping() {
        this.mappingId = "dfm_" + System.currentTimeMillis();
    }
    
    // Getters and Setters
    public String getMappingId() { return mappingId; }
    public void setMappingId(String mappingId) { this.mappingId = mappingId; }
    
    public String getSourceElementId() { return sourceElementId; }
    public void setSourceElementId(String sourceElementId) { this.sourceElementId = sourceElementId; }
    
    public String getTargetElementId() { return targetElementId; }
    public void setTargetElementId(String targetElementId) { this.targetElementId = targetElementId; }
    
    public String getDataType() { return dataType; }
    public void setDataType(String dataType) { this.dataType = dataType; }
    
    public List<String> getDataFields() { return dataFields; }
    public void setDataFields(List<String> dataFields) { this.dataFields = dataFields; }
    
    public Map<String, Object> getTransformationRules() { return transformationRules; }
    public void setTransformationRules(Map<String, Object> transformationRules) { this.transformationRules = transformationRules; }
}