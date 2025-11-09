package org.example.domain.dto.common;

import org.example.domain.entities.LLMProvider;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Base DTO class for all response objects
 * Provides common fields and functionality for response DTOs
 */
public abstract class BaseResponse {
    private String requestId;
    private LocalDateTime timestamp;
    private LLMProvider provider;
    private ResponseStatus status;
    private String message;
    private String errorCode;
    private Map<String, Object> metadata;
    
    public BaseResponse() {
        this.requestId = UUID.randomUUID().toString();
        this.timestamp = LocalDateTime.now();
        this.status = ResponseStatus.SUCCESS;
    }
    
    public BaseResponse(ResponseStatus status, String message) {
        this();
        this.status = status;
        this.message = message;
    }
    
    public BaseResponse(LLMProvider provider, ResponseStatus status, String message) {
        this(status, message);
        this.provider = provider;
    }
    
    // Getters and setters
    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    
    public LLMProvider getProvider() { return provider; }
    public void setProvider(LLMProvider provider) { this.provider = provider; }
    
    public ResponseStatus getStatus() { return status; }
    public void setStatus(ResponseStatus status) { this.status = status; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public String getErrorCode() { return errorCode; }
    public void setErrorCode(String errorCode) { this.errorCode = errorCode; }
    
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    
    // Helper methods
    public boolean isSuccessful() {
        return status == ResponseStatus.SUCCESS;
    }
    
    public boolean hasError() {
        return status == ResponseStatus.ERROR;
    }
    
    public void addMetadata(String key, Object value) {
        if (metadata == null) {
            metadata = new java.util.HashMap<>();
        }
        metadata.put(key, value);
    }
    
    public enum ResponseStatus {
        SUCCESS, ERROR, WARNING, PENDING
    }
}