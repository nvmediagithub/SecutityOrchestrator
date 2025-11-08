package org.example.domain.model.consistency;

import java.time.LocalDateTime;

/**
 * Статус проверки консистентности
 */
public class CheckStatus {
    
    private String checkId;
    private String status;
    private LocalDateTime timestamp;
    private String errorMessage;
    private int progressPercentage;
    private String currentStep;
    
    // Статусы проверки
    public static final String STATUS_STARTED = "STARTED";
    public static final String STATUS_IN_PROGRESS = "IN_PROGRESS";
    public static final String STATUS_COMPLETED = "COMPLETED";
    public static final String STATUS_FAILED = "FAILED";
    public static final String STATUS_CANCELLED = "CANCELLED";
    public static final String STATUS_AGGREGATING = "AGGREGATING";
    
    public CheckStatus() {
        this.timestamp = LocalDateTime.now();
        this.status = STATUS_STARTED;
        this.progressPercentage = 0;
    }
    
    public CheckStatus(String checkId, String status, LocalDateTime timestamp) {
        this();
        this.checkId = checkId;
        this.status = status;
        this.timestamp = timestamp;
    }
    
    public CheckStatus(String checkId, String status, LocalDateTime timestamp, String errorMessage) {
        this(checkId, status, timestamp);
        this.errorMessage = errorMessage;
    }
    
    // Вспомогательные методы
    public boolean isCompleted() {
        return STATUS_COMPLETED.equals(status);
    }
    
    public boolean isFailed() {
        return STATUS_FAILED.equals(status);
    }
    
    public boolean isInProgress() {
        return STATUS_IN_PROGRESS.equals(status) || STATUS_STARTED.equals(status) || STATUS_AGGREGATING.equals(status);
    }
    
    public boolean isSuccessful() {
        return isCompleted() && !isFailed();
    }
    
    // Getters and Setters
    public String getCheckId() { return checkId; }
    public void setCheckId(String checkId) { this.checkId = checkId; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    
    public int getProgressPercentage() { return progressPercentage; }
    public void setProgressPercentage(int progressPercentage) { this.progressPercentage = progressPercentage; }
    
    public String getCurrentStep() { return currentStep; }
    public void setCurrentStep(String currentStep) { this.currentStep = currentStep; }
}