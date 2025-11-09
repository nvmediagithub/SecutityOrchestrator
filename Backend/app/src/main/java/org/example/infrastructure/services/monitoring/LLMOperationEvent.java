package org.example.infrastructure.services.monitoring;

import java.time.Instant;

/**
 * Event fired when LLM operations are performed
 */
public class LLMOperationEvent {
    private final String operationName;
    private final long responseTimeMs;
    private final boolean successful;
    private final String errorType;
    private final Instant timestamp;
    
    public LLMOperationEvent(String operationName, long responseTimeMs, 
                           boolean successful, String errorType, Instant timestamp) {
        this.operationName = operationName;
        this.responseTimeMs = responseTimeMs;
        this.successful = successful;
        this.errorType = errorType;
        this.timestamp = timestamp;
    }
    
    public String getOperationName() {
        return operationName;
    }
    
    public long getResponseTimeMs() {
        return responseTimeMs;
    }
    
    public boolean isSuccessful() {
        return successful;
    }
    
    public String getErrorType() {
        return errorType;
    }
    
    public Instant getTimestamp() {
        return timestamp;
    }
    
    @Override
    public String toString() {
        return "LLMOperationEvent{" +
                "operationName='" + operationName + '\'' +
                ", responseTimeMs=" + responseTimeMs +
                ", successful=" + successful +
                ", errorType='" + errorType + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}