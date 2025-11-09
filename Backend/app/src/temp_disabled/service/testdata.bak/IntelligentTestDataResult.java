package org.example.application.service.testdata;

import org.example.domain.model.testdata.DataGeneratorProfile;
import org.example.infrastructure.llm.testdata.dto.*;
import org.example.infrastructure.llm.testdata.dto.GenerationMetrics;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Result object for intelligent test data generation
 */
public class IntelligentTestDataResult {
    
    private String requestId;
    private String profileId;
    private boolean success;
    private String generatedData;
    private List<Map<String, Object>> dataRecords;
    private Map<String, Object> metadata;
    private GenerationMetrics generationMetrics;
    private TestDataQualityReport qualityReport;
    private TestDataValidationResult validationResult;
    private DataGeneratorProfile generatorProfile;
    private LocalDateTime generatedAt;
    private long generationTimeMs;
    private String errorMessage;
    
    // Constructors
    public IntelligentTestDataResult() {}
    
    // Getters and Setters
    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }
    
    public String getProfileId() { return profileId; }
    public void setProfileId(String profileId) { this.profileId = profileId; }
    
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getGeneratedData() { return generatedData; }
    public void setGeneratedData(String generatedData) { this.generatedData = generatedData; }
    
    public List<Map<String, Object>> getDataRecords() { return dataRecords; }
    public void setDataRecords(List<Map<String, Object>> dataRecords) { this.dataRecords = dataRecords; }
    
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    
    public GenerationMetrics getGenerationMetrics() { return generationMetrics; }
    public void setGenerationMetrics(GenerationMetrics generationMetrics) { this.generationMetrics = generationMetrics; }
    
    public TestDataQualityReport getQualityReport() { return qualityReport; }
    public void setQualityReport(TestDataQualityReport qualityReport) { this.qualityReport = qualityReport; }
    
    public TestDataValidationResult getValidationResult() { return validationResult; }
    public void setValidationResult(TestDataValidationResult validationResult) { this.validationResult = validationResult; }
    
    public DataGeneratorProfile getGeneratorProfile() { return generatorProfile; }
    public void setGeneratorProfile(DataGeneratorProfile generatorProfile) { this.generatorProfile = generatorProfile; }
    
    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
    
    public long getGenerationTimeMs() { return generationTimeMs; }
    public void setGenerationTimeMs(long generationTimeMs) { this.generationTimeMs = generationTimeMs; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    
    // Helper methods
    public boolean hasQualityReport() {
        return qualityReport != null;
    }
    
    public boolean isValidationPassed() {
        return validationResult != null && validationResult.isValid();
    }
    
    public int getQualityScore() {
        return qualityReport != null ? qualityReport.getOverallScore() : 0;
    }
    
    public double getValidationScore() {
        return validationResult != null ? validationResult.getValidationScore() : 0.0;
    }
}