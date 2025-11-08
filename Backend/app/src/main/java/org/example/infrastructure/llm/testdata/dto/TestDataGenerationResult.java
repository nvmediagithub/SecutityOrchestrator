package org.example.infrastructure.llm.testdata.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Result object for test data generation
 */
public class TestDataGenerationResult {
    
    private String generationId;
    private String requestId;
    private boolean successful;
    private String generatedData;
    private List<Map<String, Object>> dataRecords;
    private Map<String, Object> metadata;
    private GenerationMetrics metrics;
    private TestDataQualityReport qualityReport;
    private LocalDateTime generatedAt;
    private String errorMessage;
    private long generationTimeMs;
    
    // Constructors
    public TestDataGenerationResult() {}
    
    // Getters and Setters
    public String getGenerationId() { return generationId; }
    public void setGenerationId(String generationId) { this.generationId = generationId; }
    
    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }
    
    public boolean isSuccessful() { return successful; }
    public void setSuccessful(boolean successful) { this.successful = successful; }
    
    public String getGeneratedData() { return generatedData; }
    public void setGeneratedData(String generatedData) { this.generatedData = generatedData; }
    
    public List<Map<String, Object>> getDataRecords() { return dataRecords; }
    public void setDataRecords(List<Map<String, Object>> dataRecords) { this.dataRecords = dataRecords; }
    
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    
    public GenerationMetrics getMetrics() { return metrics; }
    public void setMetrics(GenerationMetrics metrics) { this.metrics = metrics; }
    
    public TestDataQualityReport getQualityReport() { return qualityReport; }
    public void setQualityReport(TestDataQualityReport qualityReport) { this.qualityReport = qualityReport; }
    
    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    
    public long getGenerationTimeMs() { return generationTimeMs; }
    public void setGenerationTimeMs(long generationTimeMs) { this.generationTimeMs = generationTimeMs; }
}