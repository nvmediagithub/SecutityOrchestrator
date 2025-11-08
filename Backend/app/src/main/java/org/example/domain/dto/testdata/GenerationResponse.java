package org.example.domain.dto.testdata;

import org.example.domain.model.testdata.enums.GenerationScope;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * DTO for test data generation response
 * Used for POST /api/test-data/generate/{scenarioId}
 */
public class GenerationResponse {
    
    private String requestId;
    private String generationId;
    private boolean success;
    private String message;
    private String dataType;
    private GenerationScope generationScope;
    private int requestedRecordCount;
    private int actualGeneratedCount;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private long executionTimeMs;
    
    // Generated data
    private List<Map<String, Object>> generatedData;
    private String dataFormat; // JSON, XML, CSV
    private String dataPreview; // First few records for preview
    
    // Metadata
    private Map<String, Object> metadata;
    private Map<String, Object> generationConfig;
    private List<String> warnings;
    private List<String> errors;
    
    // Quality metrics
    private double qualityScore;
    private Map<String, Object> qualityMetrics;
    private boolean validated;
    private List<String> validationResults;
    
    // Context and dependencies
    private String contextId;
    private List<String> dependencyIds;
    private boolean dependenciesResolved;
    
    // Resource usage
    private long memoryUsedMB;
    private int recordsPerSecond;
    private String provider; // LLM provider used
    
    // Constructors
    public GenerationResponse() {
        this.generationId = "gen_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 10000);
        this.startedAt = LocalDateTime.now();
        this.metadata = new java.util.HashMap<>();
        this.generationConfig = new java.util.HashMap<>();
        this.warnings = new java.util.ArrayList<>();
        this.errors = new java.util.ArrayList<>();
        this.qualityMetrics = new java.util.HashMap<>();
        this.validationResults = new java.util.ArrayList<>();
        this.dependencyIds = new java.util.ArrayList<>();
    }
    
    public GenerationResponse(String requestId) {
        this();
        this.requestId = requestId;
    }
    
    // Getters and Setters
    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }
    
    public String getGenerationId() { return generationId; }
    public void setGenerationId(String generationId) { this.generationId = generationId; }
    
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public String getDataType() { return dataType; }
    public void setDataType(String dataType) { this.dataType = dataType; }
    
    public GenerationScope getGenerationScope() { return generationScope; }
    public void setGenerationScope(GenerationScope generationScope) { this.generationScope = generationScope; }
    
    public int getRequestedRecordCount() { return requestedRecordCount; }
    public void setRequestedRecordCount(int requestedRecordCount) { this.requestedRecordCount = requestedRecordCount; }
    
    public int getActualGeneratedCount() { return actualGeneratedCount; }
    public void setActualGeneratedCount(int actualGeneratedCount) { this.actualGeneratedCount = actualGeneratedCount; }
    
    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
    
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    
    public long getExecutionTimeMs() { return executionTimeMs; }
    public void setExecutionTimeMs(long executionTimeMs) { this.executionTimeMs = executionTimeMs; }
    
    public List<Map<String, Object>> getGeneratedData() { return generatedData; }
    public void setGeneratedData(List<Map<String, Object>> generatedData) { this.generatedData = generatedData; }
    
    public String getDataFormat() { return dataFormat; }
    public void setDataFormat(String dataFormat) { this.dataFormat = dataFormat; }
    
    public String getDataPreview() { return dataPreview; }
    public void setDataPreview(String dataPreview) { this.dataPreview = dataPreview; }
    
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    
    public Map<String, Object> getGenerationConfig() { return generationConfig; }
    public void setGenerationConfig(Map<String, Object> generationConfig) { this.generationConfig = generationConfig; }
    
    public List<String> getWarnings() { return warnings; }
    public void setWarnings(List<String> warnings) { this.warnings = warnings; }
    
    public List<String> getErrors() { return errors; }
    public void setErrors(List<String> errors) { this.errors = errors; }
    
    public double getQualityScore() { return qualityScore; }
    public void setQualityScore(double qualityScore) { this.qualityScore = qualityScore; }
    
    public Map<String, Object> getQualityMetrics() { return qualityMetrics; }
    public void setQualityMetrics(Map<String, Object> qualityMetrics) { this.qualityMetrics = qualityMetrics; }
    
    public boolean isValidated() { return validated; }
    public void setValidated(boolean validated) { this.validated = validated; }
    
    public List<String> getValidationResults() { return validationResults; }
    public void setValidationResults(List<String> validationResults) { this.validationResults = validationResults; }
    
    public String getContextId() { return contextId; }
    public void setContextId(String contextId) { this.contextId = contextId; }
    
    public List<String> getDependencyIds() { return dependencyIds; }
    public void setDependencyIds(List<String> dependencyIds) { this.dependencyIds = dependencyIds; }
    
    public boolean isDependenciesResolved() { return dependenciesResolved; }
    public void setDependenciesResolved(boolean dependenciesResolved) { this.dependenciesResolved = dependenciesResolved; }
    
    public long getMemoryUsedMB() { return memoryUsedMB; }
    public void setMemoryUsedMB(long memoryUsedMB) { this.memoryUsedMB = memoryUsedMB; }
    
    public int getRecordsPerSecond() { return recordsPerSecond; }
    public void setRecordsPerSecond(int recordsPerSecond) { this.recordsPerSecond = recordsPerSecond; }
    
    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
    
    // Helper methods
    public void markCompleted() {
        this.completedAt = LocalDateTime.now();
        if (this.startedAt != null) {
            this.executionTimeMs = java.time.Duration.between(this.startedAt, this.completedAt).toMillis();
        }
    }
    
    public boolean hasWarnings() {
        return warnings != null && !warnings.isEmpty();
    }
    
    public boolean hasErrors() {
        return errors != null && !errors.isEmpty();
    }
    
    public boolean isHighQuality() {
        return qualityScore >= 80.0;
    }
    
    public boolean isPartialSuccess() {
        return success && actualGeneratedCount < requestedRecordCount;
    }
    
    public void addWarning(String warning) {
        if (warnings != null && warning != null) {
            warnings.add(warning);
        }
    }
    
    public void addError(String error) {
        if (errors != null && error != null) {
            errors.add(error);
        }
    }
    
    public void addValidationResult(String result) {
        if (validationResults != null && result != null) {
            validationResults.add(result);
        }
    }
    
    public void addQualityMetric(String key, Object value) {
        if (qualityMetrics != null && key != null) {
            qualityMetrics.put(key, value);
        }
    }
    
    public void addMetadata(String key, Object value) {
        if (metadata != null && key != null) {
            metadata.put(key, value);
        }
    }
    
    public void addDependency(String dependencyId) {
        if (dependencyIds != null && dependencyId != null && !dependencyIds.contains(dependencyId)) {
            dependencyIds.add(dependencyId);
        }
    }
    
    // Static factory methods
    public static GenerationResponse success(String requestId, String dataType, int recordCount) {
        GenerationResponse response = new GenerationResponse(requestId);
        response.setSuccess(true);
        response.setMessage("Data generation completed successfully");
        response.setDataType(dataType);
        response.setRequestedRecordCount(recordCount);
        response.setActualGeneratedCount(recordCount);
        response.setQualityScore(100.0);
        response.setValidated(true);
        response.markCompleted();
        return response;
    }
    
    public static GenerationResponse failure(String requestId, String errorMessage) {
        GenerationResponse response = new GenerationResponse(requestId);
        response.setSuccess(false);
        response.setMessage(errorMessage);
        response.addError(errorMessage);
        response.markCompleted();
        return response;
    }
    
    public static GenerationResponse partialSuccess(String requestId, String dataType, int requested, int generated) {
        GenerationResponse response = new GenerationResponse(requestId);
        response.setSuccess(true);
        response.setMessage("Partial data generation completed");
        response.setDataType(dataType);
        response.setRequestedRecordCount(requested);
        response.setActualGeneratedCount(generated);
        response.addWarning("Only " + generated + " of " + requested + " records were generated");
        response.markCompleted();
        return response;
    }
    
    @Override
    public String toString() {
        return "GenerationResponse{" +
                "requestId='" + requestId + '\'' +
                ", generationId='" + generationId + '\'' +
                ", success=" + success +
                ", dataType='" + dataType + '\'' +
                ", actualGeneratedCount=" + actualGeneratedCount +
                ", executionTimeMs=" + executionTimeMs +
                '}';
    }
}