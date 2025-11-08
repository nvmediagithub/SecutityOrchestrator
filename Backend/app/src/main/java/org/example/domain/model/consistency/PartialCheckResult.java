package org.example.domain.model.consistency;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Частичный результат проверки консистентности
 */
public class PartialCheckResult {
    
    private String checkType;
    private String status;
    private Map<String, Object> checkData;
    private long processingTimeMs;
    private LocalDateTime timestamp;
    private String errorMessage;
    private boolean success;
    
    public PartialCheckResult() {
        this.timestamp = LocalDateTime.now();
        this.status = "STARTED";
        this.success = true;
    }
    
    public PartialCheckResult(String checkType) {
        this();
        this.checkType = checkType;
    }
    
    // Getters and Setters
    public String getCheckType() { return checkType; }
    public void setCheckType(String checkType) { this.checkType = checkType; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public Map<String, Object> getCheckData() { return checkData; }
    public void setCheckData(Map<String, Object> checkData) { this.checkData = checkData; }
    
    public long getProcessingTimeMs() { return processingTimeMs; }
    public void setProcessingTimeMs(long processingTimeMs) { this.processingTimeMs = processingTimeMs; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
}