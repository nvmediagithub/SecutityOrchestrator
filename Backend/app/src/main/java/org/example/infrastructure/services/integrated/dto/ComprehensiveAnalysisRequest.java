package org.example.infrastructure.services.integrated.dto;

import java.util.List;
import java.util.UUID;

/**
 * Request DTO for comprehensive analysis
 */
public class ComprehensiveAnalysisRequest {
    
    private String projectId;
    private String projectName;
    private String projectDescription;
    private List<String> openApiSources;
    private List<String> bpmnSources;
    private AnalysisOptions options;
    private String userId;
    private UUID correlationId;
    
    public ComprehensiveAnalysisRequest() {
    }
    
    public ComprehensiveAnalysisRequest(String projectId, String projectName, String projectDescription) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.projectDescription = projectDescription;
    }
    
    public static class AnalysisOptions {
        private boolean includeOpenApiAnalysis = true;
        private boolean includeBpmnAnalysis = true;
        private boolean includeIntegratedAnalysis = true;
        private boolean includeSecurityTesting = false;
        private boolean includePerformanceAnalysis = false;
        private boolean includeBusinessLogicAnalysis = true;
        
        public AnalysisOptions() {
        }
        
        public boolean isIncludeOpenApiAnalysis() {
            return includeOpenApiAnalysis;
        }
        
        public void setIncludeOpenApiAnalysis(boolean includeOpenApiAnalysis) {
            this.includeOpenApiAnalysis = includeOpenApiAnalysis;
        }
        
        public boolean isIncludeBpmnAnalysis() {
            return includeBpmnAnalysis;
        }
        
        public void setIncludeBpmnAnalysis(boolean includeBpmnAnalysis) {
            this.includeBpmnAnalysis = includeBpmnAnalysis;
        }
        
        public boolean isIncludeIntegratedAnalysis() {
            return includeIntegratedAnalysis;
        }
        
        public void setIncludeIntegratedAnalysis(boolean includeIntegratedAnalysis) {
            this.includeIntegratedAnalysis = includeIntegratedAnalysis;
        }
        
        public boolean isIncludeSecurityTesting() {
            return includeSecurityTesting;
        }
        
        public void setIncludeSecurityTesting(boolean includeSecurityTesting) {
            this.includeSecurityTesting = includeSecurityTesting;
        }
        
        public boolean isIncludePerformanceAnalysis() {
            return includePerformanceAnalysis;
        }
        
        public void setIncludePerformanceAnalysis(boolean includePerformanceAnalysis) {
            this.includePerformanceAnalysis = includePerformanceAnalysis;
        }
        
        public boolean isIncludeBusinessLogicAnalysis() {
            return includeBusinessLogicAnalysis;
        }
        
        public void setIncludeBusinessLogicAnalysis(boolean includeBusinessLogicAnalysis) {
            this.includeBusinessLogicAnalysis = includeBusinessLogicAnalysis;
        }
    }
    
    public static class OpenApiAnalysisRequest {
        private String openApiSource;
        private String analysisType;
        private AnalysisOptions options;
        
        public OpenApiAnalysisRequest() {
        }
        
        public String getOpenApiSource() {
            return openApiSource;
        }
        
        public void setOpenApiSource(String openApiSource) {
            this.openApiSource = openApiSource;
        }
        
        public String getAnalysisType() {
            return analysisType;
        }
        
        public void setAnalysisType(String analysisType) {
            this.analysisType = analysisType;
        }
        
        public AnalysisOptions getOptions() {
            return options;
        }
        
        public void setOptions(AnalysisOptions options) {
            this.options = options;
        }
    }
    
    public static class BpmnDiagramRequest {
        private String bpmnSource;
        private String diagramType;
        private AnalysisOptions options;
        
        public BpmnDiagramRequest() {
        }
        
        public String getBpmnSource() {
            return bpmnSource;
        }
        
        public void setBpmnSource(String bpmnSource) {
            this.bpmnSource = bpmnSource;
        }
        
        public String getDiagramType() {
            return diagramType;
        }
        
        public void setDiagramType(String diagramType) {
            this.diagramType = diagramType;
        }
        
        public AnalysisOptions getOptions() {
            return options;
        }
        
        public void setOptions(AnalysisOptions options) {
            this.options = options;
        }
    }
    
    // Getters and Setters
    public String getProjectId() {
        return projectId;
    }
    
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
    
    public String getProjectName() {
        return projectName;
    }
    
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
    
    public String getProjectDescription() {
        return projectDescription;
    }
    
    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }
    
    public List<String> getOpenApiSources() {
        return openApiSources;
    }
    
    public void setOpenApiSources(List<String> openApiSources) {
        this.openApiSources = openApiSources;
    }
    
    public List<String> getBpmnSources() {
        return bpmnSources;
    }
    
    public void setBpmnSources(List<String> bpmnSources) {
        this.bpmnSources = bpmnSources;
    }
    
    public AnalysisOptions getOptions() {
        return options;
    }
    
    public void setOptions(AnalysisOptions options) {
        this.options = options;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public UUID getCorrelationId() {
        return correlationId;
    }
    
    public void setCorrelationId(UUID correlationId) {
        this.correlationId = correlationId;
    }
}