package org.example.domain.dto.context;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataFlowMapping {
    
    private String sourceElement;
    
    private String targetElement;
    
    private String dataType;
    
    private String transformation;
    
    private boolean required;
    
    private String validationRules;
    
    public DataFlowMapping(String sourceElement, String targetElement, String dataType) {
        this.sourceElement = sourceElement;
        this.targetElement = targetElement;
        this.dataType = dataType;
        this.required = true;
    }
}