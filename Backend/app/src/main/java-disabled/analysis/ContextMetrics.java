package org.example.domain.entities.analysis;

/**
 * Метрики анализа контекста для BPMN процессов
 */
public class ContextMetrics {
    private final Integer totalElements;
    private final Integer complexityScore;
    private final Integer decisionPoints;
    private final Integer parallelFlows;
    private final String contextType;
    private final Double riskScore;
    private final Integer identifiedPatterns;
    private final String analysisScope;
    
    public ContextMetrics(Builder builder) {
        this.totalElements = builder.totalElements != null ? builder.totalElements : 0;
        this.complexityScore = builder.complexityScore != null ? builder.complexityScore : 0;
        this.decisionPoints = builder.decisionPoints != null ? builder.decisionPoints : 0;
        this.parallelFlows = builder.parallelFlows != null ? builder.parallelFlows : 0;
        this.contextType = builder.contextType != null ? builder.contextType : "UNKNOWN";
        this.riskScore = builder.riskScore != null ? builder.riskScore : 0.0;
        this.identifiedPatterns = builder.identifiedPatterns != null ? builder.identifiedPatterns : 0;
        this.analysisScope = builder.analysisScope != null ? builder.analysisScope : "STANDARD";
    }
    
    // Getters
    public Integer getTotalElements() { return totalElements; }
    public Integer getComplexityScore() { return complexityScore; }
    public Integer getDecisionPoints() { return decisionPoints; }
    public Integer getParallelFlows() { return parallelFlows; }
    public String getContextType() { return contextType; }
    public Double getRiskScore() { return riskScore; }
    public Integer getIdentifiedPatterns() { return identifiedPatterns; }
    public String getAnalysisScope() { return analysisScope; }
    
    // Utility methods
    public boolean isHighComplexity() {
        return complexityScore > 7;
    }
    
    public boolean hasHighRisk() {
        return riskScore > 0.7;
    }
    
    public boolean isComplexProcess() {
        return totalElements > 20 || decisionPoints > 5;
    }
    
    public String getComplexityLevel() {
        if (complexityScore <= 3) return "LOW";
        if (complexityScore <= 6) return "MEDIUM";
        return "HIGH";
    }
    
    public static class Builder {
        private Integer totalElements;
        private Integer complexityScore;
        private Integer decisionPoints;
        private Integer parallelFlows;
        private String contextType;
        private Double riskScore;
        private Integer identifiedPatterns;
        private String analysisScope;
        
        public Builder totalElements(Integer totalElements) {
            this.totalElements = totalElements;
            return this;
        }
        
        public Builder complexityScore(Integer complexityScore) {
            this.complexityScore = complexityScore;
            return this;
        }
        
        public Builder decisionPoints(Integer decisionPoints) {
            this.decisionPoints = decisionPoints;
            return this;
        }
        
        public Builder parallelFlows(Integer parallelFlows) {
            this.parallelFlows = parallelFlows;
            return this;
        }
        
        public Builder contextType(String contextType) {
            this.contextType = contextType;
            return this;
        }
        
        public Builder riskScore(Double riskScore) {
            this.riskScore = riskScore;
            return this;
        }
        
        public Builder identifiedPatterns(Integer identifiedPatterns) {
            this.identifiedPatterns = identifiedPatterns;
            return this;
        }
        
        public Builder analysisScope(String analysisScope) {
            this.analysisScope = analysisScope;
            return this;
        }
        
        public ContextMetrics build() {
            return new ContextMetrics(this);
        }
    }
}