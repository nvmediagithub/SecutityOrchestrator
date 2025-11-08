package org.example.domain.entities.llm;

import org.example.domain.entities.openapi.OpenApiSpecification;
import org.example.domain.entities.BpmnDiagram;
import org.example.domain.valueobjects.AnalysisId;
import org.example.domain.valueobjects.InconsistencyType;
import org.example.domain.valueobjects.SeverityLevel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Domain entity representing the results of LLM consistency analysis
 * between OpenAPI specifications and BPMN business processes
 */
public class ConsistencyAnalysis {
    
    private final AnalysisId analysisId;
    private final String openApiSpecId;
    private final String bpmnProcessId;
    private final AnalysisType analysisType;
    private final ConsistencyStatus status;
    private final LocalDateTime startedAt;
    private final LocalDateTime completedAt;
    private final List<SemanticMatch> semanticMatches;
    private final List<InconsistencyReport> inconsistencies;
    private final List<LLMSuggestion> suggestions;
    private final Map<String, Object> metadata;
    private final double consistencyScore;
    private final AnalysisScope scope;
    
    public ConsistencyAnalysis(Builder builder) {
        this.analysisId = builder.analysisId;
        this.openApiSpecId = builder.openApiSpecId;
        this.bpmnProcessId = builder.bpmnProcessId;
        this.analysisType = builder.analysisType;
        this.status = builder.status;
        this.startedAt = builder.startedAt;
        this.completedAt = builder.completedAt;
        this.semanticMatches = builder.semanticMatches;
        this.inconsistencies = builder.inconsistencies;
        this.suggestions = builder.suggestions;
        this.metadata = builder.metadata;
        this.consistencyScore = builder.consistencyScore;
        this.scope = builder.scope;
    }
    
    // Getters
    public AnalysisId getAnalysisId() { return analysisId; }
    public String getOpenApiSpecId() { return openApiSpecId; }
    public String getBpmnProcessId() { return bpmnProcessId; }
    public AnalysisType getAnalysisType() { return analysisType; }
    public ConsistencyStatus getStatus() { return status; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public List<SemanticMatch> getSemanticMatches() { return semanticMatches; }
    public List<InconsistencyReport> getInconsistencies() { return inconsistencies; }
    public List<LLMSuggestion> getSuggestions() { return suggestions; }
    public Map<String, Object> getMetadata() { return metadata; }
    public double getConsistencyScore() { return consistencyScore; }
    public AnalysisScope getScope() { return scope; }
    
    // Business methods
    public boolean isCompleted() {
        return status == ConsistencyStatus.COMPLETED;
    }
    
    public boolean hasInconsistencies() {
        return inconsistencies != null && !inconsistencies.isEmpty();
    }
    
    public long getCriticalInconsistenciesCount() {
        return inconsistencies.stream()
            .filter(issue -> issue.getSeverity() == SeverityLevel.CRITICAL)
            .count();
    }
    
    public long getHighInconsistenciesCount() {
        return inconsistencies.stream()
            .filter(issue -> issue.getSeverity() == SeverityLevel.HIGH)
            .count();
    }
    
    public List<InconsistencyReport> getInconsistenciesByType(InconsistencyType type) {
        return inconsistencies.stream()
            .filter(issue -> issue.getType() == type)
            .toList();
    }
    
    public List<LLMSuggestion> getSuggestionsByPriority(LLMSuggestion.SuggestionPriority priority) {
        return suggestions.stream()
            .filter(suggestion -> suggestion.getPriority() == priority)
            .toList();
    }
    
    // Builder pattern
    public static class Builder {
        private AnalysisId analysisId = AnalysisId.generate();
        private String openApiSpecId;
        private String bpmnProcessId;
        private AnalysisType analysisType = AnalysisType.SEMANTIC_COMPARISON;
        private ConsistencyStatus status = ConsistencyStatus.PENDING;
        private LocalDateTime startedAt = LocalDateTime.now();
        private LocalDateTime completedAt;
        private List<SemanticMatch> semanticMatches = List.of();
        private List<InconsistencyReport> inconsistencies = List.of();
        private List<LLMSuggestion> suggestions = List.of();
        private Map<String, Object> metadata = Map.of();
        private double consistencyScore = 0.0;
        private AnalysisScope scope = AnalysisScope.FULL;
        
        public Builder analysisId(AnalysisId analysisId) {
            this.analysisId = analysisId;
            return this;
        }
        
        public Builder openApiSpecId(String openApiSpecId) {
            this.openApiSpecId = openApiSpecId;
            return this;
        }
        
        public Builder bpmnProcessId(String bpmnProcessId) {
            this.bpmnProcessId = bpmnProcessId;
            return this;
        }
        
        public Builder analysisType(AnalysisType analysisType) {
            this.analysisType = analysisType;
            return this;
        }
        
        public Builder status(ConsistencyStatus status) {
            this.status = status;
            return this;
        }
        
        public Builder completedAt(LocalDateTime completedAt) {
            this.completedAt = completedAt;
            return this;
        }
        
        public Builder semanticMatches(List<SemanticMatch> semanticMatches) {
            this.semanticMatches = semanticMatches;
            return this;
        }
        
        public Builder inconsistencies(List<InconsistencyReport> inconsistencies) {
            this.inconsistencies = inconsistencies;
            return this;
        }
        
        public Builder suggestions(List<LLMSuggestion> suggestions) {
            this.suggestions = suggestions;
            return this;
        }
        
        public Builder metadata(Map<String, Object> metadata) {
            this.metadata = metadata;
            return this;
        }
        
        public Builder consistencyScore(double consistencyScore) {
            this.consistencyScore = consistencyScore;
            return this;
        }
        
        public Builder scope(AnalysisScope scope) {
            this.scope = scope;
            return this;
        }
        
        public ConsistencyAnalysis build() {
            if (openApiSpecId == null) {
                throw new IllegalArgumentException("OpenAPI spec ID is required");
            }
            if (bpmnProcessId == null) {
                throw new IllegalArgumentException("BPMN process ID is required");
            }
            return new ConsistencyAnalysis(this);
        }
    }
    
    // Enums
    public enum AnalysisType {
        SEMANTIC_COMPARISON,
        BUSINESS_LOGIC_VALIDATION,
        PARAMETER_CONSISTENCY,
        SECURITY_ALIGNMENT,
        ERROR_HANDLING_CONSISTENCY,
        COMPREHENSIVE_ANALYSIS
    }
    
    public enum ConsistencyStatus {
        PENDING,
        RUNNING,
        COMPLETED,
        FAILED,
        CANCELLED
    }
    
    public enum AnalysisScope {
        BASIC,
        DETAILED,
        FULL,
        SECURITY_FOCUSED,
        PERFORMANCE_FOCUSED
    }
    
    public static class SemanticMatch {
        private final String openApiElement;
        private final String bpmnElement;
        private final double similarityScore;
        private final MatchType matchType;
        private final String description;
        private final Map<String, Object> properties;
        
        public SemanticMatch(Builder builder) {
            this.openApiElement = builder.openApiElement;
            this.bpmnElement = builder.bpmnElement;
            this.similarityScore = builder.similarityScore;
            this.matchType = builder.matchType;
            this.description = builder.description;
            this.properties = builder.properties;
        }
        
        public String getOpenApiElement() { return openApiElement; }
        public String getBpmnElement() { return bpmnElement; }
        public double getSimilarityScore() { return similarityScore; }
        public MatchType getMatchType() { return matchType; }
        public String getDescription() { return description; }
        public Map<String, Object> getProperties() { return properties; }
        
        public boolean isStrongMatch() {
            return similarityScore >= 0.8;
        }
        
        public boolean isWeakMatch() {
            return similarityScore < 0.5;
        }
        
        public enum MatchType {
            EXACT,
            SEMANTIC,
            PARTIAL,
            NO_MATCH
        }
        
        public static class Builder {
            private String openApiElement;
            private String bpmnElement;
            private double similarityScore = 0.0;
            private MatchType matchType = MatchType.NO_MATCH;
            private String description = "";
            private Map<String, Object> properties = Map.of();
            
            public Builder openApiElement(String openApiElement) {
                this.openApiElement = openApiElement;
                return this;
            }
            
            public Builder bpmnElement(String bpmnElement) {
                this.bpmnElement = bpmnElement;
                return this;
            }
            
            public Builder similarityScore(double similarityScore) {
                this.similarityScore = similarityScore;
                return this;
            }
            
            public Builder matchType(MatchType matchType) {
                this.matchType = matchType;
                return this;
            }
            
            public Builder description(String description) {
                this.description = description;
                return this;
            }
            
            public Builder properties(Map<String, Object> properties) {
                this.properties = properties;
                return this;
            }
            
            public SemanticMatch build() {
                return new SemanticMatch(this);
            }
        }
    }
}