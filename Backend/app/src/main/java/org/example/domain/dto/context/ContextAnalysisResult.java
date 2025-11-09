package org.example.domain.dto.context;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContextAnalysisResult {
    
    private String resultId;
    
    private String analysisType;
    
    private ContextAnalysisMetrics metrics;
    
    private boolean isSuccessful;
    
    private String summary;
    
    private String recommendations;
    
    public ContextAnalysisMetrics getMetrics() { return metrics; }
    
    public void setMetrics(ContextAnalysisMetrics metrics) { this.metrics = metrics; }
}