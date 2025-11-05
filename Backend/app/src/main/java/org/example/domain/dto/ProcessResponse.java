package org.example.domain.dto;

import java.time.Instant;

public class ProcessResponse {
    private String id;
    private String name;
    private String version;
    private String status;
    private Instant createdAt;
    private Instant updatedAt;

    public ProcessResponse() {}

    public ProcessResponse(String id, String name, String version, String status, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.name = name;
        this.version = version;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}