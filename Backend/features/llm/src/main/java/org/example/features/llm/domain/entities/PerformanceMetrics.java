package org.example.features.llm.domain.entities;

import java.time.LocalDateTime;

/**
 * Domain entity representing performance metrics for LLM operations
 */
public class PerformanceMetrics {

    private String id;
    private String modelId;
    private String operation;
    private long responseTimeMs;
    private int tokensUsed;
    private boolean success;
    private String errorMessage;
    private LocalDateTime timestamp;

    // Default constructor
    public PerformanceMetrics() {}

    // Constructor with essential fields
    public PerformanceMetrics(String id, String modelId, String operation, long responseTimeMs, int tokensUsed, boolean success) {
        this.id = id;
        this.modelId = modelId;
        this.operation = operation;
        this.responseTimeMs = responseTimeMs;
        this.tokensUsed = tokensUsed;
        this.success = success;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public long getResponseTimeMs() {
        return responseTimeMs;
    }

    public void setResponseTimeMs(long responseTimeMs) {
        this.responseTimeMs = responseTimeMs;
    }

    public int getTokensUsed() {
        return tokensUsed;
    }

    public void setTokensUsed(int tokensUsed) {
        this.tokensUsed = tokensUsed;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}