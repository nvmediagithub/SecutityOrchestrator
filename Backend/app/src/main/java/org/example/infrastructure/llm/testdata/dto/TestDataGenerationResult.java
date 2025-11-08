package org.example.infrastructure.llm.testdata.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * DTO for LLM test data generation result
 */
public class TestDataGenerationResult {
    
    private String generationId;
    private String requestId;
    private boolean successful;
    private String errorMessage;
    private LocalDateTime generatedAt;
    
    // Generated data
    private String generatedData; // Raw LLM response
    private List<Map<String, Object>> dataRecords; // Parsed structured data
    private Map<String, Object> metadata; // Generation metadata
    
    // Quality metrics
    private TestDataQualityReport qualityReport;
    private TestDataValidationResult validationResult;
    
    // Performance metrics
    private long generationTimeMs;
    private int tokensUsed;
    private String provider;
    private String model;
    
    // Context information
    private String contextId;
    private List<String> dependencyIds;
    private boolean dependenciesResolved;
    
    // Generation details
    private GenerationMetrics metrics;
    private List<String> warnings;
    private List<String> recommendations;
    
    // Constructors
    public TestDataGenerationResult() {
        this.generationId = "result_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 10000);
        this.generatedAt = LocalDateTime.now();
        this.warnings = new java.util.ArrayList<>();
        this.recommendations = new java.util.ArrayList<>();
        this.dependencyIds = new java.util.ArrayList<>();
        this.metadata = new java.util.HashMap<>();
    }
    
    // Getters and Setters
    public String getGenerationId() { return generationId; }
    public void setGenerationId(String generationId) { this.generationId = generationId; }
    
    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }
    
    public boolean isSuccessful() { return successful; }
    public void setSuccessful(boolean successful) { this.successful = successful; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    
    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
    
    public String getGeneratedData() { return generatedData; }
    public void setGeneratedData(String generatedData) { this.generatedData = generatedData; }
    
    public List<Map<String, Object>> getDataRecords() { return dataRecords; }
    public void setDataRecords(List<Map<String, Object>> dataRecords) { this.dataRecords = dataRecords; }
    
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    
    public TestDataQualityReport getQualityReport() { return qualityReport; }
    public void setQualityReport(TestDataQualityReport qualityReport) { this.qualityReport = qualityReport; }
    
    public TestDataValidationResult getValidationResult() { return validationResult; }
    public void setValidationResult(TestDataValidationResult validationResult) { this.validationResult = validationResult; }
    
    public long getGenerationTimeMs() { return generationTimeMs; }
    public void setGenerationTimeMs(long generationTimeMs) { this.generationTimeMs = generationTimeMs; }
    
    public int getTokensUsed() { return tokensUsed; }
    public void setTokensUsed(int tokensUsed) { this.tokensUsed = tokensUsed; }
    
    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
    
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    
    public String getContextId() { return contextId; }
    public void setContextId(String contextId) { this.contextId = contextId; }
    
    public List<String> getDependencyIds() { return dependencyIds; }
    public void setDependencyIds(List<String> dependencyIds) { this.dependencyIds = dependencyIds; }
    
    public boolean isDependenciesResolved() { return dependenciesResolved; }
    public void setDependenciesResolved(boolean dependenciesResolved) { this.dependenciesResolved = dependenciesResolved; }
    
    public GenerationMetrics getMetrics() { return metrics; }
    public void setMetrics(GenerationMetrics metrics) { this.metrics = metrics; }
    
    public List<String> getWarnings() { return warnings; }
    public void setWarnings(List<String> warnings) { this.warnings = warnings; }
    
    public List<String> getRecommendations() { return recommendations; }
    public void setRecommendations(List<String> recommendations) { this.recommendations = recommendations; }
    
    // Helper methods
    public boolean hasWarnings() {
        return warnings != null && !warnings.isEmpty();
    }
    
    public boolean hasRecommendations() {
        return recommendations != null && !recommendations.isEmpty();
    }
    
    public boolean isHighQuality() {
        return qualityReport != null && qualityReport.getOverallScore() >= 80.0;
    }
    
    public boolean isValidated() {
        return validationResult != null && validationResult.isValid();
    }
    
    public void addWarning(String warning) {
        if (warnings != null && warning != null) {
            warnings.add(warning);
        }
    }
    
    public void addRecommendation(String recommendation) {
        if (recommendations != null && recommendation != null) {
            recommendations.add(recommendation);
        }
    }
    
    public void addDependency(String dependencyId) {
        if (dependencyIds != null && dependencyId != null && !dependencyIds.contains(dependencyId)) {
            dependencyIds.add(dependencyId);
        }
    }
    
    @Override
    public String toString() {
        return "TestDataGenerationResult{" +
                "generationId='" + generationId + '\'' +
                ", requestId='" + requestId + '\'' +
                ", successful=" + successful +
                ", hasData=" + (dataRecords != null && !dataRecords.isEmpty()) +
                ", qualityScore=" + (qualityReport != null ? qualityReport.getOverallScore() : "N/A") +
                '}';
    }
}