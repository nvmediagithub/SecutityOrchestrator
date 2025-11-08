package org.example.infrastructure.llm.testdata.dto;

import java.time.LocalDateTime;

/**
 * Status of test data generation
 */
public class TestDataGenerationStatus {
    
    private String generationId;
    private String status; // STARTED, GENERATING, QUALITY_ANALYSIS, VALIDATION, COMPLETED, FAILED
    private LocalDateTime timestamp;
    private String errorMessage;
    private int progress; // 0-100
    private String currentStep;
    
    // Constructors
    public TestDataGenerationStatus() {}
    
    public TestDataGenerationStatus(String generationId, String status, LocalDateTime timestamp) {
        this.generationId = generationId;
        this.status = status;
        this.timestamp = timestamp;
    }
    
    public TestDataGenerationStatus(String generationId, String status, LocalDateTime timestamp, String errorMessage) {
        this(generationId, status, timestamp);
        this.errorMessage = errorMessage;
    }
    
    // Getters and Setters
    public String getGenerationId() { return generationId; }
    public void setGenerationId(String generationId) { this.generationId = generationId; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    
    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = progress; }
    
    public String getCurrentStep() { return currentStep; }
    public void setCurrentStep(String currentStep) { this.currentStep = currentStep; }
}