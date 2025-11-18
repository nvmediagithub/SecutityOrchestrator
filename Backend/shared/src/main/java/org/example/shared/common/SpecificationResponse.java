package org.example.shared.common;

import java.time.Instant;

/**
 * Response DTO for OpenAPI Specification operations
 */
public class SpecificationResponse {
    private String id;
    private String title;
    private String version;
    private Instant loadedAt;

    public SpecificationResponse() {}

    public SpecificationResponse(String id, String title, String version, Instant loadedAt) {
        this.id = id;
        this.title = title;
        this.version = version;
        this.loadedAt = loadedAt;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Instant getLoadedAt() {
        return loadedAt;
    }

    public void setLoadedAt(Instant loadedAt) {
        this.loadedAt = loadedAt;
    }

    @Override
    public String toString() {
        return "SpecificationResponse{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", version='" + version + '\'' +
                ", loadedAt=" + loadedAt +
                '}';
    }
}