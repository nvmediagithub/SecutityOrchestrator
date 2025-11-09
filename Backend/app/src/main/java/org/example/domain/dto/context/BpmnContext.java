package org.example.domain.dto.context;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BpmnContext {
    
    private String processId;
    
    private String processName;
    
    private String version;
    
    private Map<String, DataFlowMapping> dataFlowMapping;
    
    private String description;
    
    private boolean isValid;
    
    private String validationErrors;
    
    public Map<String, DataFlowMapping> getDataFlowMapping() { return dataFlowMapping; }
    
    public void setDataFlowMapping(Map<String, DataFlowMapping> dataFlowMapping) { this.dataFlowMapping = dataFlowMapping; }
}