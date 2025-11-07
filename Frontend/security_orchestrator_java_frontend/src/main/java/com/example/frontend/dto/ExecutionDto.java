package com.example.frontend.dto;

import java.time.Instant;

public class ExecutionDto {
    private String executionId;
    private String status;
    private String message;
    private Instant completedAt;
    private String workflowId;
    private String processId;

    public ExecutionDto() {}

    public ExecutionDto(String executionId, String status, String message, Instant completedAt) {
        this.executionId = executionId;
        this.status = status;
        this.message = message;
        this.completedAt = completedAt;
    }

    // Getters and setters
    public String getExecutionId() { return executionId; }
    public void setExecutionId(String executionId) { this.executionId = executionId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Instant getCompletedAt() { return completedAt; }
    public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }

    public String getWorkflowId() { return workflowId; }
    public void setWorkflowId(String workflowId) { this.workflowId = workflowId; }

    public String getProcessId() { return processId; }
    public void setProcessId(String processId) { this.processId = processId; }
}