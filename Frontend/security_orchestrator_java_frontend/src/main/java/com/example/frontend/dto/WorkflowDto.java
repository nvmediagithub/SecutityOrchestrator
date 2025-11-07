package com.example.frontend.dto;

import java.time.Instant;
import java.util.List;

public class WorkflowDto {
    private String id;
    private String name;
    private String status;
    private Instant createdAt;
    private Instant updatedAt;
    private String processId;
    private List<String> testCaseIds;

    public WorkflowDto() {}

    public WorkflowDto(String id, String name, String status, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public String getProcessId() { return processId; }
    public void setProcessId(String processId) { this.processId = processId; }

    public List<String> getTestCaseIds() { return testCaseIds; }
    public void setTestCaseIds(List<String> testCaseIds) { this.testCaseIds = testCaseIds; }
}